/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package org.apache.mina.core.session;

import org.apache.mina.core.write.WriteRequest;

/**
 * An I/O event or an I/O request that MINA provides.
 * Most users won't need to use this class.  It is usually used by internal
 * components to store I/O events.
 *
 * @author The Apache MINA Project (dev@mina.apache.org)
 * @version $Rev: 592965 $, $Date: 2007-11-08 01:15:00 +0100 (Thu, 08 Nov 2007) $
 */
public class IoEvent implements Runnable {
    private final IoEventType type;

    private final IoSession session;

    private final Object parameter;

    public IoEvent(IoEventType type, IoSession session, Object parameter) {
        if (type == null) {
            throw new NullPointerException("type");
        }
        if (session == null) {
            throw new NullPointerException("session");
        }
        this.type = type;
        this.session = session;
        this.parameter = parameter;
    }

    public IoEventType getType() {
        return type;
    }

    public IoSession getSession() {
        return session;
    }

    public Object getParameter() {
        return parameter;
    }
    
    public void run() {
        fire();
    }

    public void fire() {
        switch (getType()) {
        case MESSAGE_RECEIVED:
        	getSession().getFirstFilterIn().messageReceived(session, getParameter());
            break;
        case MESSAGE_SENT:
        	getSession().getFirstFilterOut().messageSent(session, (WriteRequest) getParameter());
            break;
        case WRITE:
        	getSession().getFirstFilterOut().filterWrite(session, (WriteRequest) getParameter());
            break;
        case SET_TRAFFIC_MASK:
            //getSession().getFilterChain().fireFilterSetTrafficMask((TrafficMask) getParameter());
            break;
        case CLOSE:
        	getSession().getFirstFilterIn().filterClose(session);
            break;
        case EXCEPTION_CAUGHT:
        	getSession().getFirstFilterIn().exceptionCaught(session, (Throwable) getParameter());
            break;
        case SESSION_IDLE:
        	getSession().getFirstFilterIn().sessionIdle(session, (IdleStatus) getParameter());
            break;
        case SESSION_OPENED:
        	getSession().getFirstFilterIn().sessionOpened(session);
            break;
        case SESSION_CREATED:
        	getSession().getFirstFilterIn().sessionCreated(session);
            break;
        case SESSION_CLOSED:
        	getSession().getFirstFilterIn().sessionClosed(session);
            break;
        default:
            throw new IllegalArgumentException("Unknown event type: " + getType());
        }
    }

    @Override
    public String toString() {
        if (getParameter() == null) {
            return "[" + getSession() + "] " + getType().name();
        } else {
            return "[" + getSession() + "] " + getType().name() + ": "
                    + getParameter();
        }
    }
}