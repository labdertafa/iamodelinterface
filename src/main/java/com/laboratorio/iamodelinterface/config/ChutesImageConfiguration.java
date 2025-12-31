package com.laboratorio.iamodelinterface.config;

import com.laboratorio.chutes.image.ChutesImageModel;
import com.laboratorio.chutes.image.config.ChutesImageApi;
import com.laboratorio.chutes.image.config.ChutesImageOptions;
import com.laboratorio.clientapilibrary.utils.ReaderConfig;
import org.springframework.ai.image.ImageModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class ChutesImageConfiguration {
    private final ReaderConfig config = new ReaderConfig("config//ia_models_config.properties");

    @Bean
    public ChutesImageApi chutesImageApi() {
        String apiKey = this.config.getProperty("chutes_bearer_token");
        String baseUrl = this.config.getProperty("image_baseurl");
        String uri = this.config.getProperty("image_uri");

        return new ChutesImageApi(apiKey, baseUrl, uri);
    }

    @Bean
    public ChutesImageOptions chutesImageOptions() {
        String model = this.config.getProperty("image_default_model");
        Integer width = Integer.valueOf(this.config.getProperty("image_default_width"));
        Integer height = Integer.valueOf(this.config.getProperty("image_default_height"));
        Double guidanceScale = Double.valueOf(this.config.getProperty("image_default_guidanceScale"));
        Integer numInferenceSteps = Integer.valueOf(this.config.getProperty("image_default_numInferenceSteps"));

        return new ChutesImageOptions(model, width, height, guidanceScale, numInferenceSteps);
    }

    @Bean(name = "chutesImageModel")
    @Primary
    public ImageModel chutesImageModel(ChutesImageApi api, ChutesImageOptions options) {
        return new ChutesImageModel(api, options);
    }
}