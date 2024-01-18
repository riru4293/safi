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
package jp.mydns.projectk.safi.producer;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import static java.util.function.Predicate.not;
import java.util.function.Supplier;
import static java.util.stream.Collectors.toMap;
import jp.mydns.projectk.formula.Formula;
import jp.mydns.projectk.formula.FormulaExecutionException;
import jp.mydns.projectk.formula.Function;
import jp.mydns.projectk.formula.parser.Parser;
import jp.mydns.projectk.plugin.PluginExecutionException;
import jp.mydns.projectk.plugin.PluginLoader;
import jp.mydns.projectk.safi.plugin.FunctionPlugin;
import static jp.mydns.projectk.safi.util.EntryUtils.compute;
import static jp.mydns.projectk.safi.util.LambdaUtils.lastWins;
import static jp.mydns.projectk.safi.util.LambdaUtils.p;
import static jp.mydns.projectk.safi.util.LambdaUtils.toLinkedHashMap;
import jp.mydns.projectk.safi.value.TransResult;

/**
 * General transform process.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
@RequestScoped
public class TransformService {

    @Inject
    private PluginLoader<FunctionPlugin> plgLdr;

    /**
     * Transform processing.
     *
     * @author riru
     * @version 1.0.0
     * @since 1.0.0
     */
    public static interface Transformer {

        /**
         * Transform key-value content to another key-value content.
         *
         * @param src source content
         * @return transformed content
         * @since 1.0.0
         */
        TransResult transform(Map<String, String> src);
    }

    /**
     * Build a new {@code Transformer}.
     *
     * @param transdef transform definition
     * @return the {@code Transformer}
     * @throws NullPointerException if {@code transdef} is {@code null}
     * @throws ConstraintViolationException if {@code transdef} is malformed
     * @since 1.0.0
     */
    public Transformer buildTransformer(Map<@NotBlank String, @NotNull String> transdef) {
        return new TransformerImpl(Objects.requireNonNull(transdef));
    }

    class TransformerImpl implements Transformer {

        final Map<String, Formula> formulas;

        TransformerImpl(Map<String, String> transdef) {

            Map<String, Supplier<? extends Function>> functions = Collections.unmodifiableMap(plgLdr.stream().collect(
                    toMap(Entry::getKey, Entry::getValue, lastWins(), () -> new TreeMap<>(String.CASE_INSENSITIVE_ORDER))));

            var parser = new Parser(functions);

            formulas = transdef.entrySet().stream().map(compute(parser::parse)).collect(toLinkedHashMap());

        }

        /**
         * {@inheritDoc}
         *
         * @since 1.0.0
         */
        @Override
        public TransResult transform(Map<String, String> src) {

            Objects.requireNonNull(src);

            try {

                Map<String, String> transformed = formulas.entrySet().stream().map(compute(f -> f.calculate(src)))
                        .filter(p(Objects::nonNull, Entry::getValue)).collect(toLinkedHashMap());

                return new TransResult.Success(transformed, src);

            } catch (RuntimeException ex) {

                String reason = Optional.of(ex).filter(this::isExpectedException)
                        .map(Exception::getMessage).filter(not(String::isBlank))
                        .orElse("An unexpected error has occurred while calculating a formula.");

                return new TransResult.Failure(reason, src);

            }

        }

        private boolean isExpectedException(Throwable t) {
            return t instanceof FormulaExecutionException || t instanceof PluginExecutionException;
        }

    }
}
