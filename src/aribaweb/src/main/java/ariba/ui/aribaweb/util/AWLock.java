/*
    Copyright 1996-2008 Ariba, Inc.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

    $Id: //ariba/platform/ui/aribaweb/ariba/ui/aribaweb/util/AWLock.java#6 $
*/

package ariba.ui.aribaweb.util;

public class AWLock extends AWBaseObject
{
    private boolean _isLocked = false;

    public synchronized void lock ()
    {
        lockObject();
    }


    public synchronized void unlock ()
    {
        _isLocked = false;
        this.notify();
    }

    public synchronized void relock ()
    {
        lockObject();
    }

    private void lockObject() {
        while (_isLocked) {
            try {
                this.wait();
            }
            catch (InterruptedException exception) {
                // ignore
                exception = null;
            }
        }
        _isLocked = true;
    }
    
}
