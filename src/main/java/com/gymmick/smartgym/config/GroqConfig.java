package com.gymmick.smartgym.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GroqConfig {

  @Value("${groq.api.model}")
  private String model;

  @Bean
  public ChatClient chatClient(ChatClient.Builder builder) {
    OpenAiChatOptions.Builder optionsBuilder = OpenAiChatOptions.builder()
        .model(model)
        .temperature(0.4)
        .maxCompletionTokens(4096);

    return builder
        .defaultSystem(
            """

                Anda adalah Pelatih Pribadi yang berpengalaman dan ahli untuk gym.
                Buatlah rencana latihan berdasarkan konfigurasi pengguna dan peralatan gym yang tersedia.
                Anda WAJIB memberikan HANYA objek JSON yang valid dan sesuai dengan antarmuka TypeScript tersebut. Jangan sertakan format markdown atau teks tambahan apa pun.

                                The JSON Schema must be:
                                {
                                  "title": "String ,
                                  "goal": "String",
                                  "schedule": [
                                    {
                                      "dayName": "String between [Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday]",
                                      "splitType": "String between [PULL, PUSH, UPPER, LOWER, FULL_BODY]"
                                      "focus": "String (e.g., Chest & Triceps)",
                                      "estimatedDurationMin": Number,
                                      "exercises": [
                                        {
                                          "name": "String",
                                          "equipment": "String",
                                          "sets": Number,
                                          "reps": "String",
                                          "restTimeSec": Number,
                                          "notes": "String"
                                        }
                                      ]
                                    }
                                  ],
                                }

                                KETENTUAN:
                                1. HANYA gunakan peralatan yang tercantum dalam Daftar Peralatan Tersedia (Available Equipment List).
                                2. Jika suatu peralatan berstatus "MAINTENANCE" (dalam perbaikan) dan pengguna memilih opsi "Avoid Maintenance Equipment" (hindari peralatan yang sedang dalam perbaikan), JANGAN gunakan peralatan tersebut.
                                3. Sesuaikan latihan dengan fokus anatomi tubuh pengguna dan tingkat pengalaman latihan (training age) pengguna..
                                4. Gunakan bahasa Indonesia untuk nilai (value) dalam JSON yang Anda buat, namun tetap gunakan bahasa Inggris untuk nama kunci (key).
                            """)
        .defaultOptions(optionsBuilder)
        .build();
  }

}
