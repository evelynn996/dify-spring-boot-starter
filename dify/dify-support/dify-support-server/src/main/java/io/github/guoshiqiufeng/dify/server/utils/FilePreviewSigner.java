package io.github.guoshiqiufeng.dify.server.utils;

import org.springframework.util.Assert;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 文件预览签名工具类
 * 用于生成带有时间戳和签名的文件预览 URL
 *
 * 注意：URL 的有效期由 Dify 服务端的 FILES_ACCESS_TIMEOUT 配置决定（默认 300 秒）
 *
 * @author zsy
 */
public class FilePreviewSigner {
    private static final int NONCE_BYTES = 16;
    private static final String HMAC_SHA256 = "HmacSHA256";
    private static final String URL_TEMPLATE = "%s/files/%s/file-preview?%s";
    private static final String SIGNATURE_MESSAGE_TEMPLATE = "file-preview|%s|%s|%s";

    private final String fileUrl;
    private final String secretKey;
    private final SecureRandom secureRandom;

    /**
     * 构造函数
     *
     * @param fileUrl   文件服务地址
     * @param secretKey 签名密钥
     * @throws IllegalArgumentException 如果 fileUrl 或 secretKey 为空
     */
    public FilePreviewSigner(String fileUrl, String secretKey) {
        Assert.hasText(fileUrl, "fileUrl must not be empty");
        Assert.hasText(secretKey, "secretKey must not be empty");
        this.fileUrl = normalizeUrl(fileUrl);
        this.secretKey = secretKey;
        this.secureRandom = new SecureRandom();
    }

    /**
     * 构建带签名的文件 URL
     * 使用当前时间戳生成签名，URL 的有效期由服务端配置决定
     *
     * @param fileId       文件 ID
     * @param asAttachment 是否作为附件下载
     * @return 带签名的完整 URL
     */
    public String buildSignedFileUrl(String fileId, boolean asAttachment) {
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String nonce = generateNonce(NONCE_BYTES);
        return buildSignedFileUrl(fileId, asAttachment, timestamp, nonce);
    }

    /**
     * 构建带签名的文件 URL（指定 timestamp 和 nonce）
     * 主要用于测试目的
     *
     * @param fileId       文件 ID
     * @param asAttachment 是否作为附件下载
     * @param timestamp    时间戳（秒）
     * @param nonce        随机数
     * @return 带签名的完整 URL
     */
    public String buildSignedFileUrl(String fileId, boolean asAttachment, String timestamp, String nonce) {
        Assert.hasText(fileId, "fileId must not be empty");
        Assert.hasText(timestamp, "timestamp must not be empty");
        Assert.hasText(nonce, "nonce must not be empty");

        String sign = buildSignature(fileId, timestamp, nonce);

        Map<String, String> params = new LinkedHashMap<>();
        params.put("timestamp", timestamp);
        params.put("nonce", nonce);
        params.put("sign", sign);
        if (asAttachment) {
            params.put("as_attachment", "true");
        }

        String query = params.entrySet().stream()
                .map(entry -> encode(entry.getKey()) + "=" + encode(entry.getValue()))
                .collect(Collectors.joining("&"));

        return String.format(URL_TEMPLATE, fileUrl, fileId, query);
    }

    /**
     * 构建签名
     *
     * @param fileId    文件 ID
     * @param timestamp 时间戳
     * @param nonce     随机数
     * @return Base64 编码的签名
     */
    private String buildSignature(String fileId, String timestamp, String nonce) {
        String msg = String.format(SIGNATURE_MESSAGE_TEMPLATE, fileId, timestamp, nonce);
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256);
            mac.init(new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), HMAC_SHA256));
            return Base64.getUrlEncoder().encodeToString(mac.doFinal(msg.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException | InvalidKeyException ex) {
            throw new IllegalStateException("Failed to generate file preview signature", ex);
        }
    }

    /**
     * 生成随机 nonce
     *
     * @param bytes nonce 字节数
     * @return 十六进制字符串
     */
    private String generateNonce(int bytes) {
        byte[] data = new byte[bytes];
        secureRandom.nextBytes(data);
        StringBuilder builder = new StringBuilder(bytes * 2);
        for (byte value : data) {
            builder.append(String.format("%02x", value));
        }
        return builder.toString();
    }

    /**
     * URL 编码
     *
     * @param value 待编码的值
     * @return 编码后的值
     */
    private String encode(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.name());
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to URL encode value", ex);
        }
    }

    /**
     * 规范化 URL，移除末尾的斜杠
     *
     * @param url 原始 URL
     * @return 规范化后的 URL
     */
    private String normalizeUrl(String url) {
        if (url == null || url.isEmpty()) {
            return url;
        }
        if (url.endsWith("/")) {
            return url.substring(0, url.length() - 1);
        }
        return url;
    }
}
