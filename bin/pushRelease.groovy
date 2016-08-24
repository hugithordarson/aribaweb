/*
    pushRelease.groovy <cmd> - utilities to publish AW source/build/site to subversion/sourceforge

    This is normally invoked via targets in the opensourceui Makefile

    Prerequisites:
        rsync installed and in path
        svn installed and in path
*/
def usage ()
{
    println "Usage pushRelease.groovy <command>"
    println "    svn-push: push source to google code"
    println "    dist-push: push binary distribution to SourceForge"
    println "    site-push: push web site to SourceForge"
    println "    label: label release (based on version.properties)"
    System.exit(0)
}

InstallRoot = new File(System.getenv()["ARIBA_INSTALL_ROOT"])
OSAWRoot = new File(InstallRoot, "internal/opensource")
TmpDir = new File(System.getProperty("java.io.tmpdir"))

SvnRepo = "https://aribaweb.googlecode.com/svn/trunk/"
SvnProjName = "aribaweb"
SvnUserName = System.getenv()["SVN_USERNAME"]
SourceForgeUserName = System.getenv()["SF_USERNAME"]
DistFile = new File(OSAWRoot, "dist").listFiles().find { f -> f.name.endsWith(".zip") && !f.name.contains("-src-") }

SrcDirs = ["../util/core", "../util/expr",
           "aribaweb", "widgets", "metaui",
           "demoshell", "opensourceui", "awreload",
           "ideplugin", "metaui-jpa"]



if (args.length != 1) usage()
String cmd = args[0]
if (!["svn-push", "dist-push", "site-push", "label"].contains(cmd)) usage()

checkPrereqs()

if ("svn-push" == cmd) pushSource()
if ("dist-push" == cmd) pushDist()
if ("site-push" == cmd) pushSite()
if ("label" == cmd) labelRelease()
System.exit(0)

def checkPrereqs ()
{
    assert InstallRoot.exists(), "Cannot find ARIBA_INSTALL_ROOT: ${InstallRoot}"
    assert new File(OSAWRoot, "lib/ariba.widgets.jar").exists(), "Jars are missing from lib dir"
    assert TmpDir.exists(), "Java returning bogus temp dir: ${TmpDir}"

    println "OSAWRoot = " + OSAWRoot + "\n\n\n";
    assert new File(OSAWRoot, "src/aribaweb/ariba/ui/aribaweb/core/AWComponent.java").exists(), \
        "Missing source code (/src dir).  Make sure to `gnu make dev-full-clean-package`"

    assert new File(OSAWRoot, "src/aribaweb/ariba/ui/aribaweb/core/AWComponent.java").exists(), \
        "Missing source code (/src dir).  Make sure to `gnu make dev-full-clean-package`"

    assert new File(OSAWRoot, "src/aribaweb/resource/ariba/resource/de/strings/ariba.ui.aribaweb.core.csv").exists(), \
        "Missing localized strings file.  Make sure to `gnu make dev-full-clean-package`"

    assert DistFile?.exists(),\
        "Built distribution zip file not found.  Make sure to `gnu make dev-full-clean-package`"

    // Check for availability of rsync
    assert exec("rsync -h", true, null), "Failed to find/invoke rsync -- make sure that it is installed and in your PATH"
}

def exec (String command, boolean captureOutput, File dir)
{
    def buf = new StringBuffer()
    println "Executing: `${command}` ${dir?'dir: ' + dir.absolutePath:''}"
    Process p = dir ? command.execute(null, dir) : command.execute()
    if (captureOutput) p.consumeProcessOutputStream(buf)
    else p.consumeProcessOutput(System.out, System.err)

    def status = p.waitFor()
    assert (status == 0), "Non-zero exit status for command: '${command}': ${status}"
    return buf.toString()
}

def cygwinPath (path) 
{
    // replace d:/ to /cygdrive/d on windows
    return path.replaceAll("\\\\", '/').replaceAll(/(.):/, {x, a -> "/cygdrive/$a"} )
}

def pushSource ()
{
    // check prereqs
    assert exec("svn help", true, null), "Failed to find/invoke snv (subversion) -- make sure that it is installed and in your PATH"
    assert SvnUserName, "SVN_USERNAME environment variable not defined (should be the google user name admin user on google code project)"

    File stageDir = new File(TmpDir, "aw-svn-stage")
    File ProjDir = new File(stageDir, SvnProjName)

    if (stageDir.exists()) {
        println "Deleting $stageDir..."
        stageDir.deleteDir()
        assert !stageDir.exists(), "Unable to remove $stageDir"
    }
    stageDir.mkdir()
   
    // pull from svn
    exec("svn co $SvnRepo $SvnProjName --username $SvnUserName", false, stageDir)

    def svnLabel = labelFromFile(ProjDir.getPath() + "/src/version.properties")

    // overlay from perforce
    osawDirPath = cygwinPath(OSAWRoot.getCanonicalPath())
    exec("rsync -av --delete --exclude=.svn/ --exclude=/conf --exclude=/work \
	        --exclude=/webapps --exclude=/dist --exclude=/ide  --exclude=/docs --exclude=/site \
	        --exclude=/lib/*.jar --exclude=/tools/ant --exclude=/tools/tomcat \
            --exclude=/bin/pushRelease.groovy \
            $osawDirPath/ .",
	    false, ProjDir)

    // remove empty dirs (i.e. that contain only a ".snv" dir
    println "Deleting empty directories..."
    ProjDir.eachDirRecurse { dir ->
        if (dir.name == ".svn" && dir.parentFile.listFiles().size() == 1) {
            println "  ... deleting ${dir.parentFile}"
            dir.delete()
            dir.parentFile.delete()
        }
    }

    // process adds and deletes
    String stat = exec("svn status .", true, ProjDir)
    List adds = (stat =~ /(?m)^\?\s+(.+)$/).collect { all, name -> name }
    if (adds) exec("svn add ${adds.join(" ")}", false, ProjDir)
    List removes = (stat =~ /(?m)^\!\s+(.+)$/).collect { all, name -> name }
    if (removes) exec("svn rm ${removes.join(" ")}", false, ProjDir)

    // generate changes.txt
    println "Getting latest change for svn label $svnLabel"

    List changes = []
    // fin latest change in each component
    SrcDirs.each {
        File dir = new File("../", it)
        path = dir.getCanonicalPath()
        def lastChange = exec("p4 changes -m1 $path/...@$svnLabel", true, stageDir)
        // add one to exclude last change
        if (lastChange) {
            lastChange = lastChange.split(" ")[1].toInteger() + 1
            compChanges = exec("p4 changes $path...@$lastChange,#head", true, stageDir)
            if (compChanges) {
                compChanges.eachLine { change ->
                    // dedup
                    if (!changes.contains(change)) {
                        changes.add(change)
                    }
                }
            }
        }
    }
    fullChangesFile = new File(stageDir, "fullchanges.txt")
    changesFile = new File(stageDir, "changes.txt")
    println("Changes since $svnLabel:")
    changes.each { change ->
        println("    " + change)
    }

    changeWriter = changesFile.newPrintWriter()
    fullChangeWriter = fullChangesFile.newPrintWriter()
    changes.each() { change ->
        changeId = change.split(" ")[1]
        desc = exec("p4 describe -s $changeId", true, stageDir)
        fullChangeWriter.println(desc)
        fullChangeWriter.println("--------------------------------------------------------------------")
        foundDesc = false
        writeDesc = false
        // extract OSAW section
        desc.eachLine { line ->
            if (line =~ /.*OSAW:.*/) {
                writeDesc = true
                foundDesc = true
            }
            else if (line =~ /[^\:]*:[^\:]*/) {
                writeDesc = false
            }          
            if (writeDesc) {
                changeWriter.println(line)
            }
        }
        if (!foundDesc) {
            changeWriter.println("no description found for $changeId")
        }
        changeWriter.println("--------------------------------------------------------------------")
    }
    fullChangeWriter.close()
    changeWriter.close()

    println("Full changes descriptions in ${fullChangesFile.getCanonicalPath()}")      
    println("SVN changes descriptions in ${changesFile.getCanonicalPath()}")      

    println ""
    println "New SVN Status: "
    exec("svn status .", false, ProjDir)
    println ""
    println "To commit:"
    println "   1) cd $ProjDir"
    println "   2) svn commit -F ../changes.txt"
    println "Remember to run Run `gnu make dev-label-release` to label push"
    println ""
    
}

def pushDist ()
{
    assert SourceForgeUserName, "SF_USERNAME environment variable not defined (should of form 'cfederighi')"
    println "Invoking rsync over ssh..."
    distRoot = new File(OSAWRoot, "dist")
    distDirPath = cygwinPath(distRoot.getCanonicalPath())
    exec("rsync -avP -e ssh ${distDirPath}/ ${SourceForgeUserName}@frs.sourceforge.net:uploads/", false, null)
    println "Done!"
    println "Now go to https://sourceforge.net/project/admin/editpackages.php?group_id=227584 and Add Release"
}

def pushSite ()
{
    def docbase = cygwinPath(OSAWRoot.getCanonicalPath());
    assert SourceForgeUserName, "SF_USERNAME environment variable not defined (should of form 'cfederighi')"
    println "Pushing javadoc and site via rsync over ssh..."
    exec("rsync -avPz -e ssh $docbase/docs/api $docbase/site/ ${SourceForgeUserName},aribaweb@web.sourceforge.net:htdocs/", false, null)
    println "Done!"
    println "Visit http://aribaweb.org to see the new files"
}

def labelFromFile (filepath)
{
    println "Getting version from $filepath"
    def props = new java.util.Properties();
    props.load(new FileInputStream(filepath))
    def version = props["version"]
    assert version, "Failed to read version from src/version.properties"
    return "opensourceui-$version"
}

def labelRelease ()
{
    def P4User = System.getenv()["P4USER"]
    assert P4User, "P4User undefined"
    label = labelFromFile("src/version.properties")

    println "Creating label: $label..."
    Process p = "p4 label -i".execute()
    // Process p = "cat".execute()
    p.consumeProcessOutput(System.out, System.err)
    p.withWriter { writer ->
        writer <<
"""Label: $label
Owner: $P4User
Description:
       Open Source AribaWeb release label.
Options: unlocked
View:
       //ariba/...
"""
    }
    assert (p.waitFor() == 0), "Non-zero exit status for p4 label"

    println "Adding component files to label..."
    SrcDirs.each {
        File dir = new File("../", it)
        println "Adding files in ${dir.getCanonicalPath()}"
        // exec("p4 labelsync -l $label ...#have", false, dir.getCanonicalFile())
        exec("p4 labelsync -l $label ...#have", false, dir.getCanonicalFile())
    }
}
