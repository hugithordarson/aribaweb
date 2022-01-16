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

    $Id: //ariba/platform/util/core/ariba/util/core/PoorThreadLocalState.java#5 $
*/

package ariba.util.core;

import java.util.Map;

/**
    Do not make this class anything other than package private. Would
    be callers should use StateFactory.

*/
class PoorThreadLocalState implements State, StateFactoryInterface
{
    final Map threadToValue = MapUtil.map();
    private static final Object NullObject = new Object();

        // these methods can have improved synchronization, but it
        // probably isn't worth it.
    public void set (Object value)
    {
        if (value == null) {
            value = NullObject;
        }
        synchronized (threadToValue) {
            threadToValue.put(Thread.currentThread(),
                              value);
        }
    }
    public Object get ()
    {
        Object retval;
        synchronized (threadToValue) {
            retval = threadToValue.get(Thread.currentThread());
        }
        if (retval == NullObject) {
            return null;
        }
        return retval;
    }

    public State createState ()
    {
        return new PoorThreadLocalState();
    }
}
