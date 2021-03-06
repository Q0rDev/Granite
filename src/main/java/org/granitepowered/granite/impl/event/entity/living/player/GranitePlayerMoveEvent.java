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

package org.granitepowered.granite.impl.event.entity.living.player;

import org.granitepowered.granite.impl.entity.player.GranitePlayer;
import org.spongepowered.api.event.entity.living.player.PlayerMoveEvent;
import org.spongepowered.api.world.Location;

public class GranitePlayerMoveEvent extends GranitePlayerEvent implements PlayerMoveEvent {

    Location old;
    Location new_;

    public GranitePlayerMoveEvent(GranitePlayer player, Location old, Location new_) {
        super(player);
        this.old = old;
        this.new_ = new_;
    }

    @Override
    public Location getOldLocation() {
        return old;
    }

    @Override
    public Location getNewLocation() {
        return new_;
    }
}
