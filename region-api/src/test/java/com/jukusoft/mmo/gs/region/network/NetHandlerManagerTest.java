package com.jukusoft.mmo.gs.region.network;

import com.jukusoft.vertx.serializer.test.TestObject;
import com.jukusoft.vertx.serializer.test.TestObjectWithoutType;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class NetHandlerManagerTest {

    @Test
    public void testConstructor () {
        new NetHandlerManager();
    }

    @Test (expected = IllegalStateException.class)
    public void testFindUnregisteredHandler () {
        HandlerManager manager = new NetHandlerManager();
        manager.findHandler(TestObjectWithoutType.class);
    }

    @Test
    public void testRegister () {
        HandlerManager manager = new NetHandlerManager();

        //register handler
        manager.register(TestObject.class, (msg, user, cid, conn) -> {
            //do something
        });

        //check, if handler is registered
        NetMessageHandler handler = manager.findHandler(TestObject.class);
        assertNotNull(handler);
    }

    @Test (expected = IllegalStateException.class)
    public void testRegisterWithoutMessageType () {
        HandlerManager manager = new NetHandlerManager();

        //register handler
        manager.register(TestObjectWithoutType.class, (msg, user, cid, conn) -> {
            //do something
        });
    }

    @Test
    public void testGetMessageTypeAnnotation () {
        HandlerManager manager = new NetHandlerManager();

        //register handler
        ((NetHandlerManager) manager).getMessageTypeAnnotation(TestObject.class);
    }

    @Test (expected = IllegalStateException.class)
    public void testGetNullMessageTypeAnnotation () {
        HandlerManager manager = new NetHandlerManager();

        //register handler
        ((NetHandlerManager) manager).getMessageTypeAnnotation(TestObjectWithoutType.class);
    }

    @Test
    public void testRegisterAndUnregister () {
        HandlerManager manager = new NetHandlerManager();

        //register handler
        manager.register(TestObject.class, (msg, user, cid, conn) -> {
            //do something
        });

        //check, if handler is registered
        NetMessageHandler handler = manager.findHandler(TestObject.class);
        assertNotNull(handler);

        //unregister handler
        manager.unregister(TestObject.class);
    }

    @Test
    public void testRegisterAndUnregister1 () {
        HandlerManager manager = new NetHandlerManager();

        //register handler
        manager.register(TestObject.class, (msg, user, cid, conn) -> {
            //do something
        });

        //check, if handler is registered
        NetMessageHandler handler = manager.findHandler(TestObject.class);
        assertNotNull(handler);

        //unregister handler
        manager.unregister(TestObject.class);
        assertNull(manager.findHandler(TestObject.class));
    }

}
