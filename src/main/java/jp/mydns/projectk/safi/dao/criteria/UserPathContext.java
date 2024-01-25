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

import jakarta.persistence.criteria.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import static jp.mydns.projectk.safi.constant.AttKey.*;
import jp.mydns.projectk.safi.dao.criteria.CriteriaPathContext.AbstractCriteriaPathContext;
import jp.mydns.projectk.safi.entity.ContentEntity_;
import jp.mydns.projectk.safi.entity.UserEntity;
import jp.mydns.projectk.safi.entity.embedded.AttsEmb_;

/**
 * Path information of the {@code t_user} table.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
public class UserPathContext extends AbstractCriteriaPathContext {

    /**
     * Constructor.
     *
     * @param path path to entity
     * @throws NullPointerException if {@code path} is {@code null}
     * @since 1.0.0
     */
    public UserPathContext(Path<UserEntity> path) {
        super(buildMapping(path));
    }

    private static Map<String, Path<String>> buildMapping(Path<UserEntity> p) {

        Map<String, Path<String>> m = new LinkedHashMap<>();

        m.put("id", p.get(ContentEntity_.id));
        m.put("name", p.get(ContentEntity_.name));
        m.put(ATT01.toString(), p.get(ContentEntity_.attsEmb).get(AttsEmb_.att01));
        m.put(ATT02.toString(), p.get(ContentEntity_.attsEmb).get(AttsEmb_.att02));
        m.put(ATT03.toString(), p.get(ContentEntity_.attsEmb).get(AttsEmb_.att03));
        m.put(ATT04.toString(), p.get(ContentEntity_.attsEmb).get(AttsEmb_.att04));
        m.put(ATT05.toString(), p.get(ContentEntity_.attsEmb).get(AttsEmb_.att05));
        m.put(ATT06.toString(), p.get(ContentEntity_.attsEmb).get(AttsEmb_.att06));
        m.put(ATT07.toString(), p.get(ContentEntity_.attsEmb).get(AttsEmb_.att07));
        m.put(ATT08.toString(), p.get(ContentEntity_.attsEmb).get(AttsEmb_.att08));
        m.put(ATT09.toString(), p.get(ContentEntity_.attsEmb).get(AttsEmb_.att09));
        m.put(ATT10.toString(), p.get(ContentEntity_.attsEmb).get(AttsEmb_.att10));

        return Collections.unmodifiableMap(m);
    }
}
