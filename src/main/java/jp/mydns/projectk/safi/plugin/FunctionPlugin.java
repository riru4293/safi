/*
 * Copyright (c) 2024, Project-K
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
package jp.mydns.projectk.safi.plugin;

import jp.mydns.projectk.formula.FormulaExecutionException;
import jp.mydns.projectk.formula.Function;
import jp.mydns.projectk.plugin.PluginExecutionException;

/**
 * Formula-function plugin interface.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
public interface FunctionPlugin extends SafiPlugin, Function {

    /**
     * Execute this function.
     *
     * @param args function arguments
     * @return result of execute function. It may be {@code null}.
     * @throws NullPointerException if {@code args} is {@code null} or an element of {@code args} is {@code null}
     * @throws PluginExecutionException if processing cannot be continued
     * @since 1.0.0
     */
    @Override
    String execute(Argument... args);

    /**
     * Abstract implements of the {@code FunctionPlugin}.
     *
     * @author riru
     * @version 1.0.0
     * @since 1.0.0
     */
    abstract class AbstractFunctionPlugin implements FunctionPlugin {

        /**
         * {@inheritDoc}
         *
         * @throws PluginExecutionException if processing cannot be continued
         * @since 1.0.0
         */
        @Override
        public final String execute(Argument... args) {
            try {
                return calculate(getArgumentScheme().requireValid(args));
            } catch (FormulaExecutionException | PluginExecutionException ex) {
                throw ex;
            } catch (Throwable ignore) {
                // Note:
                // Cause exception does not wrap because it may be contaminated by an exception class
                // loaded with another class loader.
                throw new PluginUnknownException();
            }
        }

        /**
         * {@inheritDoc}
         *
         * @param args valid number arguments
         * @return result of function. It may be {@code null}.
         * @throws PluginExecutionException if processing cannot be continued
         * @since 1.0.0
         */
        protected abstract String calculate(Argument... args);
    }
}
