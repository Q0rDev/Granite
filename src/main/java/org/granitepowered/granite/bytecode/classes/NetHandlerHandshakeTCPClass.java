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

package org.granitepowered.granite.bytecode.classes;

import org.granitepowered.granite.bytecode.BytecodeClass;
import org.granitepowered.granite.bytecode.Proxy;
import org.granitepowered.granite.bytecode.ProxyCallbackInfo;
import org.granitepowered.granite.impl.status.ConnectionInfo;
import org.granitepowered.granite.impl.status.GraniteStatusClient;
import org.granitepowered.granite.mc.MCNetHandlerHandshakeTCP;
import org.granitepowered.granite.mc.MCPacketHandshake;
import org.granitepowered.granite.util.MinecraftUtils;

public class NetHandlerHandshakeTCPClass extends BytecodeClass {

    public NetHandlerHandshakeTCPClass() {
        super("NetHandlerHandshakeTCP");
    }

    @Proxy(methodName = "processHandshake")
    public Object processHandshake(ProxyCallbackInfo<MCNetHandlerHandshakeTCP> info) throws Throwable {
        ConnectionInfo connectionInfo = (GraniteStatusClient) MinecraftUtils.wrap(info.getCaller().fieldGet$networkManager());
        MCPacketHandshake packetHandshake = ((MCPacketHandshake) info.getArguments()[0]);
        connectionInfo.setVersion(packetHandshake.fieldGet$protocolVersion());
        connectionInfo.setVirtualHost(packetHandshake.fieldGet$ip(), packetHandshake.fieldGet$port());
        return info.callback();
    }
}
