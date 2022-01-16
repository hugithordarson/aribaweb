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

    $Id: //ariba/platform/util/core/ariba/util/io/CSVConsumer.java#5 $
*/

package ariba.util.io;

import java.util.List;

/**
    The CSVReader calls through this interface to dispose of the lines
    that it reads.

    CSVReader was originally written so that the method called back
    was in a subclass.  As a transition step, CSVReader itself
    implements this interface so that subclasses can be the consumer.
        @aribaapi documented
*/
public interface CSVConsumer
{
    /**
        Called once per CSV line read.

        @param path the CSV source file
        @param lineNumber the current line being reported, 1-based.
        @param line a List of tokens parsed from a one line in the file
        @aribaapi documented
    */
    public void consumeLineOfTokens (String path,
                                     int    lineNumber,
                                     List line);
}


