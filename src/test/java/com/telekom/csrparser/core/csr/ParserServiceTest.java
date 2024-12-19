package com.telekom.csrparser.core.csr;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
class ParserServiceTest {

    private static final String csr = "-----BEGIN CERTIFICATE REQUEST-----\n"
            + "MIIFEjCCAvoCAQAwgZ8xCzAJBgNVBAYTAkRFMQ8wDQYDVQQIDAZIZXNzZW4xEjAQ\n"
            + "BgNVBAcMCURhcm1zdGFkdDEcMBoGA1UECgwTRGV1dHNjaGUgVGVsZWtvbSBBRzEU\n"
            + "MBIGA1UECwwLVGVsIElUIEdtYkgxEjAQBgNVBAMMCWRvbWFpbi5kZTEjMCEGCSqG\n"
            + "SIb3DQEJARYUd2VibWFzdGVyQGV4YW1wbGUuZGUwggIiMA0GCSqGSIb3DQEBAQUA\n"
            + "A4ICDwAwggIKAoICAQDFjmyA+bZQBaOCwpjWZRjprG4LhobqFFvPSbkKBDXxFlOH\n"
            + "pra1Mc2HTxNNMYhPmONTA6bOpHTyTOKJAkItCW68Nwta1dHFCz7J5k0w336pB/pU\n"
            + "9yQpPPhMtJo3OibClFV2MHct2K5A+0HPyHsu5FoQ39ZPanHm6W9e4/3FFdB1zcha\n"
            + "xwm/d6v8oELCLWg3ECpcBURg4NMlQUxOhzfpgxkSEzYDu0YPXLF4yx9UOrK6mDl+\n"
            + "q1d9QapkR5zttPWZYAsuq7qh/lVN2i+jvF1Vd5bEZdbZLFxb1aYATucLGrbuy/rc\n"
            + "9P+A8i/utZK4FmZ+9eT8nkYuCV3As+I4ViS1a0fP+MTydwia/txq2mL1W25xScG9\n"
            + "3LAqfDf5Ed7RnSZ59HC1Zy+ZWFmzUgevuF8G5ZsvfQvO8Ssscp6K5UWdQ3OXoyMF\n"
            + "wT/GdMTVvBU2g49pwbbCxnyNbMMwIfx9h3B0RiVcz+BNdS/DaVf7Elft+QqlaUtD\n"
            + "jUSyiafpKd11T81s2czKkVHIngj85cb4xb1iAlBaLvdQ7bAjjbZAh4SOLZ+v9di5\n"
            + "Pr9kpDem7iq1uTGm2I3xqrjjbtMnUtLkKHqQUBIOvqKPvVfh5CbnsANrMk1jYwCa\n"
            + "bXwJUCZRzpnwclI+OQcnFM6M2G78s7/AhcqFf2ir0IGUT7y+geTBhvDR39QfewID\n"
            + "AQABoC0wFAYJKoZIhvcNAQkCMQcMBURUIEFHMBUGCSqGSIb3DQEJBzEIDAZwYXNz\n"
            + "d2QwDQYJKoZIhvcNAQELBQADggIBAIXxy/Kp9F8fNBK/Ri7zTPiyshiM+YIgGo9q\n"
            + "+tNvKD8MsXLc6OLdKiBWzu/JmOAUX5ajfX0ex9UWStrdJORwhHIdDvHom7pgvWRz\n"
            + "pyUcN5Sg/+e6jgteUywEhILn8th1xK2dxApSYnzLz/ldNtLin6Z3bwZB+yKQ28wk\n"
            + "IQiJPS6oYPURGWbCGNZusH4JM9suzGAMBfP3yVwy3/iFi/3+FojiDW9NnRgMXrvY\n"
            + "zpu5LMAPCfAAtz3v95GT9x//54ItHa6O2o/aPekJvV6NXca9m4X6CFlhejq5hAhH\n"
            + "H7qDwXndEVkv62xGrp3L0HZUWCT2k1zvNddfeQ+ICTEeLJaJWtdqv55rBffcC84j\n"
            + "EdGt72fY84apNKUJnDR2H9xZT5axUu3YyJqGQZA2VO/eegEWMAluOqp8xe3JFzQT\n"
            + "3MgRrlLPwv4cxzvYJ3IaZ7wr2bnUPJV3bqcvTRrNxqDi6h/KRHXMmGwvSXfzETEw\n"
            + "wMXkoo1w2L9KLFk/+wkmEgugh1n9eEYZXAb3apHWMwdTY0Ca6D3CgRisbdr+4SFn\n"
            + "rs4h+0EX4V2zlGfguRxHe9tSeWP3DQBu9UiN94eJc8ZaDjWuNVmMhH/zXxne63JI\n"
            + "cROp6BPmElEbG8TLRI82FrrcNFOxMO1HbdGZXLmszJgUQFl5JyACb0F+mVlzbv9I\n"
            + "5M5P+jNV\n"
            + "-----END CERTIFICATE REQUEST-----\n";

    @Test
    void getContent() {
        // given
        InputStream stream = new ByteArrayInputStream(csr.getBytes(StandardCharsets.UTF_8));
        ParserService parserService = new ParserService();

        // when
        List<CSRContent> csrContents =  parserService.getContent(stream);

        // then
        assertEquals("RSA", csrContents.get(0).value);
        assertEquals("DE", csrContents.get(1).value);
        assertEquals("Hessen", csrContents.get(2).value);
        assertEquals("Darmstadt", csrContents.get(3).value);
        assertEquals("Deutsche Telekom AG", csrContents.get(4).value);
        assertEquals("Tel IT GmbH", csrContents.get(5).value);
        assertEquals("domain.de", csrContents.get(6).value);
        assertEquals("webmaster@example.de", csrContents.get(7).value);
    }
}