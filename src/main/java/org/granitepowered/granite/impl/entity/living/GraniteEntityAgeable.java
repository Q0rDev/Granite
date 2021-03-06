/*
 * License (MIT)
 *
 * Copyright (c) 2014-2015 Granite Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.granitepowered.granite.impl.entity.living;

import org.granitepowered.granite.mc.MCEntityAgeable;
import org.spongepowered.api.entity.living.Ageable;

public class GraniteEntityAgeable<T extends MCEntityAgeable> extends GraniteEntityCreature<T> implements Ageable {

    public GraniteEntityAgeable(T obj) {
        super(obj);
    }

    @Override
    public int getAge() {
        return obj.getGrowingAge();
    }

    @Override
    public void setAge(int age) {
        obj.setGrowingAge(age);
    }

    @Override
    public void setBaby() {
        if (getAge() >= 0) {
            setAge(-24000);
        }
    }

    @Override
    public void setAdult() {
        if (getAge() < 0) {
            setAge(0);
        }
    }

    @Override
    public boolean isBaby() {
        return getAge() < 0;
    }

    @Override
    public boolean canBreed() {
        return getAge() == 0;
    }

    @Override
    public void setBreeding(boolean breeding) {
        if (breeding) {
            setAge(0);
        } else if (getAge() >= 0) {
            setAge(6000);
        }
    }

    @Override
    public void setScaleForAge() {
        obj.setScaleForAge(getAge() < 0);
    }
}
