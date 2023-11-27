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
        return "Gift Certificate was inserted: " + giftCertService.addCert(giftCert);
    }

    @GetMapping()
    public String getAllGiftCerts() {
        return giftCertService.getAllGiftCerts().toString();
    }

    @GetMapping("/search")
    public String searchGiftCertsBy(
            @RequestParam(required = false) String tagName,
            @RequestParam(required = false) String certNamePart,
            @RequestParam(required = false) String certDescPart,
            @RequestParam(required = false) String sortByDate, // values: ASC/DESC
            @RequestParam(required = false) String sortByName // values: ASC/DESC
    ) {
        return giftCertService.searchGiftCertsBy(tagName, certNamePart, certDescPart, sortByDate, sortByName).toString();
    }

    @GetMapping("/{namesParam}")
    public String getGiftCertsByNames(@PathVariable String namesParam) {
        String[] names = namesParam.trim().split("\\s*,\\s*");
        return giftCertService.getGiftCertsByNames(names).toString();
    }

    @PatchMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public String updateGiftCert(@RequestBody GiftCert giftCert) {
        return "Gift Certificate was updated: " + giftCertService.updateCert(giftCert);
    }

    @DeleteMapping("/{namesParam}")
    public String deleteGiftCert(@PathVariable String namesParam) {
        String[] names = namesParam.trim().split("\\s*,\\s*");
        return String.format("%d GiftCert(s) were deleted by names: %s", giftCertService.deleteGiftCert(names), Arrays.asList(names));
    }
}
