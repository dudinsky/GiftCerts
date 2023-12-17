package org.dudinskiy.giftcert.controller;

import org.dudinskiy.giftcert.entity.GiftCert;
import org.dudinskiy.giftcert.service.GiftCertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/giftCerts")
public class GiftCertController {

    @Autowired
    GiftCertService giftCertService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public String addGiftCert(@RequestBody GiftCert giftCert) {
        try {
            return "Gift Certificate was inserted: " + giftCertService.addCert(giftCert);
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }
    }

    @GetMapping()
    public String getAllGiftCerts() {
        try {
            return giftCertService.getAllGiftCerts().toString();
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }
    }

    @GetMapping("/search")
    public String searchGiftCertsBy(
            @RequestParam(required = false) String tagName,
            @RequestParam(required = false) String certNamePart,
            @RequestParam(required = false) String certDescPart,
            @RequestParam(required = false) String sortByDate, // values: ASC/DESC
            @RequestParam(required = false) String sortByName // values: ASC/DESC
    ) {
        try {
            return giftCertService.searchGiftCertsBy(tagName, certNamePart, certDescPart, sortByDate, sortByName).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }
    }

    @GetMapping("/{namesParam}")
    public String getGiftCertsByNames(@PathVariable String namesParam) {
        String[] names = namesParam.trim().split("\\s*,\\s*");
        try {
            return giftCertService.getGiftCertsByNames(names).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }
    }

    @PatchMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public String updateGiftCert(@RequestBody GiftCert giftCert) {
        try {
            return "Gift Certificate was updated: " + giftCertService.updateCert(giftCert);
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }
    }

    @DeleteMapping("/{namesParam}")
    public String deleteGiftCert(@PathVariable String namesParam) {
        String[] names = namesParam.trim().split("\\s*,\\s*");
        try {
            return String.format("%d GiftCert(s) were deleted by names: %s", giftCertService.deleteGiftCert(names), Arrays.asList(names));
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }
    }
}
