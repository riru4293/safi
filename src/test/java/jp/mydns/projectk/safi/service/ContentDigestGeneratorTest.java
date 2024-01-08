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
package jp.mydns.projectk.safi.service;

import jakarta.validation.Validator;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import jp.mydns.projectk.safi.constant.AttName;
import static jp.mydns.projectk.safi.constant.AttName.*;
import jp.mydns.projectk.safi.value.ValidityPeriod;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import test.ValidatorParameterResolver;

/**
 * Test of class ContentDigestGenerator.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
@ExtendWith(ValidatorParameterResolver.class)
class ContentDigestGeneratorTest {

    ContentDigestGeneratorTest() {
    }

    /**
     * Test of generate method.
     *
     * @since 1.0.0
     */
    @Test
    void testGenerate(Validator validator) throws NoSuchAlgorithmException {

        String expect = toSha256("""
            [null,"hello",true,"2000-01-01T00:00:00","2999-12-31T23:59:59",false\
            ,"a01","a02",null,null,null,null,null,null,null,"a10"]""".getBytes(StandardCharsets.UTF_8));

        Map<AttName, String> atts = Map.of(ATT01, "a01", ATT02, "a02", ATT10, "a10");

        ValidityPeriod vp = new ValidityPeriod.Builder().build(validator);

        String result = new ContentDigestGenerator().generate(null, "hello", true, vp, atts);

        assertThat(result).isEqualTo(expect);

    }

    /**
     * Test of generate method. If contains unexpected element within source.
     *
     * @since 1.0.0
     */
    @Test
    void testGenerate_ContainsUnexpectedElement(Validator validator) throws NoSuchAlgorithmException {

        var instance = new ContentDigestGenerator();

        assertThatIllegalArgumentException().isThrownBy(() -> instance.generate(Map.of("att01", "value")))
                .withMessage("Unexpected type as digest source.");

    }

    /**
     * Test constructor. If unsupported SHA256.
     *
     * @since 1.0.0
     */
    @Test
    void testConstructor_UnsupportedSha256() {
        try (MockedStatic<MessageDigest> mock = Mockito.mockStatic(MessageDigest.class)) {
            mock.when(() -> MessageDigest.getInstance("SHA-256")).thenThrow(NoSuchAlgorithmException.class);
            assertThatIllegalStateException().isThrownBy(() -> new ContentDigestGenerator());
        }
    }

    private String toSha256(byte... bytes) throws NoSuchAlgorithmException {
        return String.format("%040x", new BigInteger(1, MessageDigest.getInstance("SHA-256").digest(bytes)));
    }

}
