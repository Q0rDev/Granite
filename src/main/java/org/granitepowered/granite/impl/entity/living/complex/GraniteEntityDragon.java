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

package org.granitepowered.granite.impl.entity.living.complex;

import com.google.common.base.Optional;
import org.granitepowered.granite.impl.entity.GraniteEntityEnderCrystal;
import org.granitepowered.granite.impl.entity.living.GraniteEntityLiving;
import org.granitepowered.granite.mc.MCEntityDragon;
import org.granitepowered.granite.mc.MCEntityDragonPart;
import org.granitepowered.granite.util.MinecraftUtils;
import org.spongepowered.api.entity.EnderCrystal;
import org.spongepowered.api.entity.living.complex.EnderDragon;
import org.spongepowered.api.entity.living.complex.EnderDragonPart;

import java.util.Set;
import java.util.TreeSet;

public class GraniteEntityDragon extends GraniteEntityLiving<MCEntityDragon> implements EnderDragon {

    public GraniteEntityDragon(MCEntityDragon obj) {
        super(obj);
    }

    @Override
    public Set<EnderDragonPart> getParts() {
        Set<EnderDragonPart> enderDragonParts = new TreeSet<>();
        for (MCEntityDragonPart dragonPart : obj.fieldGet$dragonPartArray()) {
            enderDragonParts.add((EnderDragonPart) MinecraftUtils.wrap(dragonPart));
        }
        return enderDragonParts;
    }

    @Override
    public Optional<EnderCrystal> getHealingCrystal() {
        return Optional.fromNullable((EnderCrystal) MinecraftUtils.wrap(obj.fieldGet$healingEnderCrystal()));
    }

    @Override
    public void setHealingCrystal(EnderCrystal enderCrystal) {
        obj.fieldSet$healingEnderCrystal(MinecraftUtils.unwrap((GraniteEntityEnderCrystal) enderCrystal));
    }
}
