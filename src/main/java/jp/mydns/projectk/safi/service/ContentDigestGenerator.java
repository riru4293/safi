package jp.mydns.projectk.safi.service;

import jakarta.enterprise.context.RequestScoped;
import jakarta.json.Json;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import jp.mydns.projectk.safi.constant.AttKey;
import jp.mydns.projectk.safi.util.JsonUtils;
import jp.mydns.projectk.safi.value.ContentValue;
import jp.mydns.projectk.safi.value.ValidityPeriod;

/**
 * Digest value generator for the {@link ContentValue}.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
@RequestScoped
public class ContentDigestGenerator implements ContentValue.DigestGenerator {

    private final MessageDigest sha256;

    /**
     * Construct by CDI.
     *
     * @throws IllegalStateException if unsupported SHA256 as {@link MessageDigest}
     * @since 1.0.0
     */
    public ContentDigestGenerator() {
        try {
            sha256 = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Generate a digest value.
     *
     * @param sources source values
     * @return digest value. It format is SHA256.
     * @throws NullPointerException when {@code sources} is {@code null}
     * @throws IllegalArgumentException if contains an unexpected element in {@code sources}
     * @since 1.0.0
     */
    @Override
    public String generate(Object... sources) {
        Objects.requireNonNull(sources);

        var builder = Json.createArrayBuilder();

        Stream.of(sources).forEachOrdered(v -> {
            switch (v) {

                case null ->
                    builder.addNull();

                case String s ->
                    builder.add(s);

                case Boolean b ->
                    builder.add(b);

                case ValidityPeriod p -> {
                    builder.add(JsonUtils.toJsonValue(p.getFrom()));
                    builder.add(JsonUtils.toJsonValue(p.getTo()));
                    builder.add(p.isBan());
                }

                case Map<?, ?> m when m.keySet().stream().allMatch(k -> k instanceof AttKey) -> {
                    Stream.of(AttKey.values()).map(m::get).forEachOrdered(a -> Optional.ofNullable(a)
                            .map(Object::toString).ifPresentOrElse(builder::add, builder::addNull));
                }

                default ->
                    throw new IllegalArgumentException("Unexpected type as digest source.");

            }
        });

        return String.format("%040x", new BigInteger(1, sha256.digest(builder.build().toString().getBytes(StandardCharsets.UTF_8))));
    }
}
