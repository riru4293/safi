/*
 * Copyright (c) 2025, Project-K
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package jp.mydns.projectk.safi.test;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.spi.BeanManager;
import java.util.Hashtable;
import javax.naming.spi.NamingManager;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.naming.Binding;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.Context;
import javax.naming.NameClassPair;
import javax.naming.NamingException;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;

/**
 * Simple JNDI server for testing.
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
@RequestScoped
public class JndiServer {

    private final Map<String, Object> map = new ConcurrentHashMap<>();

    /**
     * Initialize JNDI context. Second runs are not supported.
     *
     * @throws IllegalStateException if failed initialize JNDI context
     * @since 3.0.0
     */
    @PostConstruct
    public void init() {
        try {
            if (!NamingManager.hasInitialContextFactoryBuilder()) {
                NamingManager.setInitialContextFactoryBuilder(a -> b -> new CustomContext());
            }
        } catch (NamingException ex) {
            throw new IllegalStateException("Failed set initial context factory of the JNDI.", ex);
        }

        try (var ctx = new CloseableInitialContext();) {
            ctx.createSubcontext("java:comp");
        } catch (NamingException ex) {
            throw new IllegalStateException("Failed create sub context java:comp to the JNDI.", ex);
        }
    }

    /**
     * Bind the {@code BeanManager} to JNDI.
     *
     * @param beanManager the {@code BeanManager}
     * @throws IllegalStateException if the JNDI context has not yet been initialized or has already been closed
     * @since 3.0.0
     */
    public void bindBeanManager(BeanManager beanManager) {
        try (var ctx = new CloseableInitialContext();) {
            ctx.bind("java:comp/BeanManager", beanManager);
        } catch (NamingException ex) {
            throw new IllegalStateException("Failed bind bean manager to the JNDI.", ex);
        }
    }

    private class CustomContext implements Context {

        @Override
        public Object lookup(Name name) throws NamingException {
            return lookup(name.toString());
        }

        @Override
        public Object lookup(String name) throws NamingException {
            if (!map.containsKey(name)) {
                throw new NamingException("Name not found: " + name);
            }
            return map.get(name);
        }

        @Override
        public void bind(Name name, Object obj) throws NamingException {
            bind(name.toString(), obj);
        }

        @Override
        public void bind(String name, Object obj) throws NamingException {
            map.put(name, obj);
        }

        @Override
        public void close() {
            // Do nothing.
        }

        @Override
        public void rebind(Name name, Object obj) {
            // Do nothing.
        }

        @Override
        public void rebind(String name, Object obj) {
            // Do nothing.
        }

        @Override
        public void unbind(Name name) {
            // Do nothing.
        }

        @Override
        public void unbind(String name) {
            // Do nothing.
        }

        @Override
        public void rename(Name oldName, Name newName) {
            // Do nothing.
        }

        @Override
        public void rename(String oldName, String newName) {
            // Do nothing.
        }

        @Override
        public NamingEnumeration<NameClassPair> list(Name name) {
            return null;
        }

        @Override
        public NamingEnumeration<NameClassPair> list(String name) {
            return null;
        }

        @Override
        public NamingEnumeration<Binding> listBindings(Name name) {
            return null;
        }

        @Override
        public NamingEnumeration<Binding> listBindings(String name) {
            return null;
        }

        @Override
        public void destroySubcontext(Name name) {
            // Do nothing.
        }

        @Override
        public void destroySubcontext(String name) {
            // Do nothing.
        }

        @Override
        public Context createSubcontext(Name name) {
            return null;
        }

        @Override
        public Context createSubcontext(String name) {
            return null;
        }

        @Override
        public Object lookupLink(Name name) {
            return null;
        }

        @Override
        public Object lookupLink(String name) {
            return null;
        }

        @Override
        public NameParser getNameParser(Name name) {
            return null;
        }

        @Override
        public NameParser getNameParser(String name) {
            return null;
        }

        @Override
        public Name composeName(Name name, Name prefix) {
            return null;
        }

        @Override
        public String composeName(String name, String prefix) {
            return null;
        }

        @Override
        public Object addToEnvironment(String propName, Object propVal) {
            return null;
        }

        @Override
        public Object removeFromEnvironment(String propName) {
            return null;
        }

        @Override
        @SuppressWarnings("UseOfObsoleteCollectionType")
        public Hashtable<?, ?> getEnvironment() {
            return null;
        }

        @Override
        public String getNameInNamespace() throws NamingException {
            return null;
        }
    }

    private class CloseableInitialContext extends InitialContext implements AutoCloseable {

        private CloseableInitialContext() throws NamingException {
        }
    }
}
