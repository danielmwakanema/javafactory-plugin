package io.github.javafactoryplugindev.plugin.openai;


import com.openai.errors.UnauthorizedException;
import io.github.javafactoryplugindev.plugin.openai.storage.OpenAiKeyStorage;
import com.intellij.openapi.project.Project;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletionCreateParams;


public class DefaultPromptRunner implements PromptRunner {

    private static DefaultPromptRunner INSTANCE = new DefaultPromptRunner();

    public static PromptRunner getInstance() {
        if (INSTANCE == null)
            INSTANCE = new DefaultPromptRunner();

        return INSTANCE;
    }

    private DefaultPromptRunner() {
    }

    @Override
    public String call(Project project, String systemPrompt, String userPrompt) {

        try {
            var keyStorage = OpenAiKeyStorage.getInstance(project);
            var key = keyStorage.getDecodedKey();

            var client = createClient(key);

            var params = ChatCompletionCreateParams.builder()
                    .addDeveloperMessage(systemPrompt)
                    .addUserMessage(userPrompt)
                    .model(ChatModel.GPT_4O)
                    .build();

            var chatCompletion = client.chat().completions().create(params);

            var content = chatCompletion.choices().get(0).message().content();


            System.out.println("generated = \n" + content.get());
            return content.get();
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            throw new OpenAiCallFailedException(" Incorrect API key provided, You can find your API key at https://platform.openai.com/account/api-keys. ", OpenAiCallFailedException.ErrorType.KEY_FAILED);
        } catch (Exception e) {
            e.printStackTrace();
            throw new OpenAiCallFailedException(" Unexpected error occurred. Please try again later. ");
        }

    }

    private OpenAIClient createClient(String key) {
        return OpenAIOkHttpClient.builder()
                .apiKey(key)
                .build();
    }


}
