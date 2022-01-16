/*
    Copyright 1996-2009 Ariba, Inc.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

    $Id: //ariba/platform/util/core/ariba/util/io/TruncationException.java#1 $
*/

package ariba.util.io;

/*
 * This class is to allow truncation of certain strings which get overly large.
 * Specifically it used by the size limiting serializeObject call in FormattingSerializer.
 * The writer will throw this exception to indicate it has reached the length limit.
 */
public class TruncationException extends RuntimeException
{
    public TruncationException (String message)
    {
        super(message);
    }
}
