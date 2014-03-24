/**
 * Copyright (c) 2013-2014, JCabi.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the jcabi.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jcabi.github;

import com.jcabi.github.mock.MkGithub;
import com.jcabi.http.Request;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.request.ApacheRequest;
import java.net.HttpURLConnection;
import javax.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link RtReference}.
 *
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @checkstyle MultipleStringLiterals (500 lines)
 */
public final class RtReferenceTest {

    /**
     * RtReference should be able to execute patch.
     * @throws Exception - If something goes wrong.
     */
    @Test
    public void patchesContent() throws Exception {
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_OK,
                "{\"ref\":\"refs/heads/featureA\"}"
            )
        ).start();
        final Reference reference = new RtReference(
            new ApacheRequest(container.home()), repo(), "refs/heads/featureA"
        );
        try {
            reference.patch(
                Json.createObjectBuilder().add("sha", "abcdef12345")
                .add("force", "false").build()
            );
            MatcherAssert.assertThat(
                container.take().method(),
                Matchers.equalTo(Request.PATCH)
            );
        } finally {
            container.stop();
        }
    }

    /**
     * RtReference should be able to fetch its json.
     * @throws Exception - if something goes wrong.
     */
    @Test
    public void fetchesContent() throws Exception {
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_OK,
                "{\"ref\":\"refs/heads/featureB\"}"
            )
        ).start();
        final Reference reference = new RtReference(
            new ApacheRequest(container.home()), repo(), "refs/heads/featureB"
        );
        try {
            MatcherAssert.assertThat(
                reference.json().getString("ref"),
                Matchers.is("refs/heads/featureB")
            );
        } finally {
            container.stop();
        }
    }

    /**
     * RtReference should be able to return its ref.
     * @throws Exception - If something goes wrong.
     */
    @Test
    public void returnsRef() throws Exception {
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_OK,
                "{\"ref\":\"refs/heads/featureC\"}"
            )
        ).start();
        final Reference reference = new RtReference(
            new ApacheRequest(container.home()), repo(), "refs/heads/featureC"
        );
        try {
            MatcherAssert.assertThat(
                reference.ref(),
                Matchers.is("refs/heads/featureC")
            );
        } finally {
            container.stop();
        }
    }

    /**
     * RtReference should be able to return its owner repo.
     * @throws Exception - If something goes wrong.
     */
    @Test
    public void returnsOwner() throws Exception {
        final Repo owner = repo();
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_OK,
                "{\"ref\":\"refs/heads/featureD\"}"
            )
        ).start();
        final Reference reference = new RtReference(
            new ApacheRequest(container.home()), owner, "refs/heads/featureD"
        );
        try {
            MatcherAssert.assertThat(
                reference.repo(),
                Matchers.is(owner)
            );
        } finally {
            container.stop();
        }
    }

    /**
     * This method returns a Repo for testing.
     * @return Repo - a repo to be used for test.
     * @throws Exception - if anything goes wrong.
     */
    private static Repo repo() throws Exception {
        return new MkGithub().repos().create(
            Json.createObjectBuilder().add("name", "test").build()
        );
    }

}
