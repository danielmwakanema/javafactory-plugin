package io.github.javafactoryplugindev.plugin.openai;

import io.github.javafactoryplugindev.plugin.openai.storage.OpenAiKeyStorage;
import com.intellij.openapi.project.Project;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.core.http.HttpResponseFor;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;

public class OpenAiKeyChecker {

    public static boolean canConnect(Project project) {
        HttpResponseFor<ChatCompletion> response = null;
        try {
            var keyStorage = OpenAiKeyStorage.getInstance(project);
            var client = OpenAIOkHttpClient.builder()
                    .apiKey(keyStorage.getDecodedKey())
                    .build();

            var params = ChatCompletionCreateParams.builder()
                    .addDeveloperMessage("hello world")
                    .model(ChatModel.GPT_3_5_TURBO_16K)
                    .build();

            response = client.chat().completions()
                    .withRawResponse()
                    .create(params);

            return response.statusCode() == 200;
        } catch (Exception e) {
            return false;
        } finally {
            if(response!=null)
                response.close();
        }
    }
}
