package com.telekom.csrparser.core.csr;


import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;


@Service
public class ParserService {

    private final Logger log = LoggerFactory.getLogger( getClass() );

    private static final String COUNTRY = "2.5.4.6";
    private static final String STATE = "2.5.4.8";
    private static final String LOCALE = "2.5.4.7";
    private static final String ORGANIZATION = "2.5.4.10";
    private static final String ORGANIZATION_UNIT = "2.5.4.11";
    private static final String COMMON_NAME = "2.5.4.3";


    public List<CSRContent> getContent(InputStream csrStream) {
        PKCS10CertificationRequest csr = convertPemToPKCS10CertificationRequest(csrStream);

        List<CSRContent> result = new ArrayList<>();
        if (csr == null) {
            log.error("fail conversion of Pem to PKCS10 Certification Request");
            result.add( new CSRContent("ERROR: ", "fail conversion of Pem to PKCS10 Certification Request") );
        } else {
            PublicKey publicKey = null;
            try {
                PKCS10CertificationRequest jcaCertRequest = new JcaPKCS10CertificationRequest(csr.getEncoded()).setProvider("BC");
                publicKey = ((JcaPKCS10CertificationRequest) jcaCertRequest).getPublicKey();
            } catch (IOException | InvalidKeyException | NoSuchAlgorithmException  e) {
                log.error( e.getMessage() );
                result.add( new CSRContent("ERROR: ", "problem during CSR decryption") );
            }

            if ( result.isEmpty() ) {
                result.add( new CSRContent("Public Key Algorithm", publicKey.getAlgorithm()) );

                X500Name x500Name = csr.getSubject();
                result.add( new CSRContent("C - COUNTRY_NAME", getX500Field(COUNTRY, x500Name)) );
                result.add( new CSRContent("ST - STATE_PROVINCE_NAME", getX500Field(STATE, x500Name)) );
                result.add( new CSRContent("L - LOCALITY_NAME", getX500Field(LOCALE, x500Name)) );
                result.add( new CSRContent("O - ORGANIZATION_NAME", getX500Field(ORGANIZATION, x500Name)) );
                result.add( new CSRContent("OU - ORGANIZATION_UNIT", getX500Field(ORGANIZATION_UNIT, x500Name)) );
                result.add( new CSRContent("CN - COMMON_NAME", getX500Field(COMMON_NAME, x500Name)) );

                if ( x500Name.getRDNs(BCStyle.EmailAddress).length != 0 ) {
                    RDN cn = x500Name.getRDNs(BCStyle.EmailAddress)[0];
                    result.add( new CSRContent("E - EMAIL_ADDRESS", cn.getFirst().getValue().toString() ) );
                }
            }
        }
        return result;
    }

    private String getX500Field(String asn1ObjectIdentifier, X500Name x500Name) {
        RDN[] rdnArray = x500Name.getRDNs(new ASN1ObjectIdentifier(asn1ObjectIdentifier));

        String retVal = null;
        for (RDN item : rdnArray) {
            retVal = item.getFirst().getValue().toString();
        }
        return retVal;
    }

    private PKCS10CertificationRequest convertPemToPKCS10CertificationRequest(InputStream pem) {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        PKCS10CertificationRequest csr = null;
        ByteArrayInputStream pemStream = null;

        pemStream = (ByteArrayInputStream) pem;

        Reader pemReader = new BufferedReader(new InputStreamReader(pemStream));
        PEMParser pemParser = null;
        try {
            pemParser = new PEMParser(pemReader);
            Object parsedObj = pemParser.readObject();
            System.out.println("PemParser returned: " + parsedObj);
            if (parsedObj instanceof PKCS10CertificationRequest) {
                csr = (PKCS10CertificationRequest) parsedObj;
            }
        } catch (IOException ex) {
            log.error("IOException, convertPemToPublicKey", ex);
        } finally {
            if (pemParser != null) {
                IOUtils.closeQuietly(pemParser);
            }
        }
        return csr;
    }

}
