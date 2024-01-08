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
package jp.mydns.projectk.safi.value;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import org.junit.jupiter.api.Test;

/**
 * Test of class RequestContext.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
class RequestContextTest {

    /**
     * Test of getProcessName method.
     *
     * @since 1.0.0
     */
    @Test
    void testGetProcessName() {
        var instance = new RequestContext();
        instance.setProcessName("value");
        assertThat(instance.getProcessName()).isEqualTo("value");
    }

    /**
     * Test of getProcessName method. If not available.
     *
     * @since 1.0.0
     */
    @Test
    void testGetProcessName_NotAvailable() {
        var instance = new RequestContext();
        assertThatIllegalStateException().isThrownBy(() -> instance.getProcessName())
                .withMessage("Value has not been set yet.");
    }

    /**
     * Test of setProcessName method.
     *
     * @since 1.0.0
     */
    @Test
    void testSetProcessName() {
        var instance = new RequestContext();
        assertThatCode(() -> instance.setProcessName("value")).doesNotThrowAnyException();
    }

    /**
     * Test of setProcessName method. If available.
     *
     * @since 1.0.0
     */
    @Test
    void testSetProcessName_Available() {
        var instance = new RequestContext();
        instance.setProcessName("value");
        assertThatIllegalStateException().isThrownBy(() -> instance.setProcessName("value"))
                .withMessage("Value has already set.");
    }

    /**
     * Test of setProcessName method. If blank.
     *
     * @since 1.0.0
     */
    @Test
    void testSetProcessName_Blank() {
        var instance = new RequestContext();
        assertThatIllegalArgumentException().isThrownBy(() -> instance.setProcessName(null))
                .withMessage("Set blank is not allowed.");
        assertThatIllegalArgumentException().isThrownBy(() -> instance.setProcessName(""))
                .withMessage("Set blank is not allowed.");
    }

    /**
     * Test of getAccountId method.
     *
     * @since 1.0.0
     */
    @Test
    void testGetAccountId() {
        var instance = new RequestContext();
        instance.setAccountId("value");
        assertThat(instance.getAccountId()).isEqualTo("value");
    }

    /**
     * Test of getAccountId method. If not available.
     *
     * @since 1.0.0
     */
    @Test
    void testGetAccountId_NotAvailable() {
        var instance = new RequestContext();
        assertThatIllegalStateException().isThrownBy(() -> instance.getAccountId())
                .withMessage("Value has not been set yet.");
    }

    /**
     * Test of setAccountId method.
     *
     * @since 1.0.0
     */
    @Test
    void testSetAccountId() {
        var instance = new RequestContext();
        assertThatCode(() -> instance.setAccountId("value")).doesNotThrowAnyException();
    }

    /**
     * Test of setAccountId method. If available.
     *
     * @since 1.0.0
     */
    @Test
    void testSetAccountId_Available() {
        var instance = new RequestContext();
        instance.setAccountId("value");
        assertThatIllegalStateException().isThrownBy(() -> instance.setAccountId("value"))
                .withMessage("Value has already set.");
    }

    /**
     * Test of setAccountId method. If blank.
     *
     * @since 1.0.0
     */
    @Test
    void testSetAccountId_Blank() {
        var instance = new RequestContext();
        assertThatIllegalArgumentException().isThrownBy(() -> instance.setAccountId(null))
                .withMessage("Set blank is not allowed.");
        assertThatIllegalArgumentException().isThrownBy(() -> instance.setAccountId(""))
                .withMessage("Set blank is not allowed.");
    }

    /**
     * Test of toString method.
     *
     * @since 1.0.0
     */
    @Test
    void testToString() {
        var instance = new RequestContext();
        instance.setProcessName("p");
        instance.setAccountId("a");
        String result = instance.toString();
        assertThat(result).isEqualTo("RequestContext{processName=p, accountId=a}");
    }
}
