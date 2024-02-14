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
package jp.mydns.projectk.safi.dao.criteria;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import java.util.List;
import java.util.Objects;
import static java.util.function.Predicate.not;
import java.util.stream.Stream;
import jp.mydns.projectk.safi.value.Condition;
import jp.mydns.projectk.safi.value.FilteringOperation;

/**
 * Build the {@code Predicate} from the {@code Condition}.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
public class PredicateBuilder {

    private final CriteriaBuilder cb;
    private final CriteriaPathContext pathCtx;

    /**
     * Constructor.
     *
     * @param cb the {@code CriteriaBuilder}
     * @param pathCtx the {@code CriteriaPathContext}
     * @throws NullPointerException if any argument is {@code null}
     * @since 1.0.0
     */
    public PredicateBuilder(CriteriaBuilder cb, CriteriaPathContext pathCtx) {
        this.cb = Objects.requireNonNull(cb);
        this.pathCtx = Objects.requireNonNull(pathCtx);
    }

    /**
     * Resolve the {@code Predicate}.
     *
     * @param condition the {@code Condition}
     * @return predicate
     * @throws NullPointerException if any argument is {@code null}
     * @since 1.0.0
     */
    public Predicate build(Condition condition) {
        return Objects.requireNonNull(condition).isMulti()
                ? buildMulti(condition.asMulti())
                : buildSingle(condition.asSingle());
    }

    Predicate buildMulti(Condition.Multi condition) {
        return buildMulti(condition.getOperation().asMulti(), condition.getChildren());
    }

    Predicate buildMulti(FilteringOperation.Multi operation, List<Condition> conditions) {
        Stream<Predicate> singlePreds = conditions.stream().filter(not(Condition::isMulti))
                .map(Condition::asSingle).map(this::buildSingle);

        Stream<Predicate> multiPreds = conditions.stream().filter(Condition::isMulti)
                .map(Condition::asMulti).map(this::buildMulti);

        Predicate[] preds = Stream.concat(singlePreds, multiPreds).toArray(Predicate[]::new);

        if (preds.length < 1) {
            return cb.and(preds); // Note: if zero predicate, "and" becomes true.
        }

        return switch (operation) {
            case OR ->
                cb.or(preds);
            case NOT_OR ->
                cb.or(preds).not();
            case AND ->
                cb.and(preds);
        };
    }

    Predicate buildSingle(Condition.Single condition) {

        Path<String> p = pathCtx.of(condition.getName());
        String v = condition.getValue();
        FilteringOperation.Single op = condition.getOperation().asSingle();

        return switch (op) {
            case EQUAL ->
                cb.equal(p, v);
            case FORWARD_MATCH ->
                cb.like(p, escapeWildcard(v) + "%");
            case PARTIAL_MATCH ->
                cb.like(p, "%" + escapeWildcard(v) + "%");
            case BACKWARD_MATCH ->
                cb.like(p, "%" + escapeWildcard(v));
            case LESS_THAN ->
                cb.lessThan(p, v);
            case GRATER_THAN ->
                cb.greaterThan(p, v);
            case IS_NULL ->
                cb.isNull(p);
        };
    }

    String escapeWildcard(String v) {
        return v.replace("%", "\\%").replace("_", "\\_");
    }
}
