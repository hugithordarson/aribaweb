/*
    Copyright 2013 Ariba, Inc.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

    $Id: //ariba/platform/util/core/ariba/util/core/CompositePredicate.java#1 $
*/

package ariba.util.core;


import java.util.HashSet;
import java.util.Set;


public abstract class CompositePredicate<T> extends Predicate<T>
{
    protected Set<Predicate<T>> _predicates;

    public CompositePredicate(Set<Predicate<T>> predicates)
    {
        _predicates = new HashSet<Predicate<T>>();
        _predicates.addAll(predicates);
    }

    abstract public boolean evaluate(T t);

}
