package org.dudinskiy.giftcert.controller.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@ComponentScan("org.dudinskiy.giftcert")
//@Configuration
@EnableWebMvc // needed for @PathVariable in org.springframework to work, won't be needed for boot
public class WebConfig {
}
