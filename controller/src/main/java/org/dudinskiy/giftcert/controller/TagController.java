package org.dudinskiy.giftcert.controller;

import org.dudinskiy.giftcert.entity.Tag;
import org.dudinskiy.giftcert.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/tags")
public class TagController {

    @Autowired
    TagService tagService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public String addTag(@RequestBody Tag tag) {
        return "Tag was inserted: " + tagService.addTag(tag);
    }

    @GetMapping()
    public String getAllTags() {
        return tagService.getAllTags().toString();
    }

    @GetMapping("/{namesParam}")
    public String getTagsByNames(@PathVariable String namesParam) {
        String[] names = namesParam.trim().split("\\s*,\\s*");
        return tagService.getTagsByNames(names).toString();
    }

    @DeleteMapping("/{namesParam}")
    public String deleteTags(@PathVariable String namesParam) {
        String[] names = namesParam.trim().split("\\s*,\\s*");
        return String.format("%d Tag(s) were deleted by names: %s", tagService.deleteTags(names), Arrays.asList(names));
    }
}
