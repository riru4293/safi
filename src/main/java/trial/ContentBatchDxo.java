/*
 * Copyright (c) 2022, Project-K
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
package trial;

import hm.orz.denken.newapp9._interface.content.Content;
import hm.orz.denken.newapp9.entity.tenant.content.ContentEntity;
import hm.orz.denken.newapp9.server.bean.ImportationValue;
import hm.orz.denken.newapp9.server.bean.TransResult;
import jakarta.validation.ConstraintViolationException;

/**
 * Data exchange interface for batch processing.
 *
 * @param <E> Entity type
 * @param <V> Value type
 *
 * @author riru
 * @since 9.0
 */
public interface ContentBatchDxo<E extends ContentEntity, V extends Content> {

    /**
     * Exchange to entity from value. If create a new persistence.
     *
     * @param value value
     * @return entity
     * @throws NullPointerException if {@code value} is {@code null}
     */
    E toEntity(ImportationValue<V> value);

    /**
     * Exchange to entity from value. If update the persisted.
     *
     * @param value value
     * @param entity managed original entity
     * @return entity
     * @throws NullPointerException if any argument is {@code null}
     */
    E toEntity(V value, E entity);

    /**
     * Exchange to value of logical deletion from entity.
     *
     * @param entity entity
     * @return value that is logical deletion
     * @throws NullPointerException if {@code entity} is {@code null} when
     * building
     */
    V toLogicalDeletion(E entity);

    /**
     * Exchange to value from the transform result.
     *
     * @param transformed transform result
     * @return value. Please note that it has been minimally validated
     * @throws NullPointerException if {@code transformed} is {@code null}
     * @throws ConstraintViolationException if occurred constraint violations
     * when building
     */
    ImportationValue<V> toValue(TransResult.Success transformed);

    /**
     * Exchange to value from next value and current entity. Only the version is
     * inherited from the current entity to the next value.
     *
     * @param next next value
     * @param current current entity
     * @return merged content that next value and version of the current entity
     * @throws NullPointerException if any argument is {@code null}
     */
    ImportationValue<V> toValue(ImportationValue<V> next, E current);

    /**
     * Rebuild content using the current application time.
     * {@code RangePermission} and digest value are subject to change, and
     * footer values are lost, but others are immutable.
     *
     * @param entity before rebuilding
     * @return after rebuilding. It is a separate instance from {@code entity}.
     * @throws NullPointerException if {@code entity} is {@code null}
     */
    E rebuild(E entity);
}
