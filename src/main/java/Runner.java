import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import org.apache.commons.text.StringSubstitutor;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Runner {

    public static void main(String[] args) {

        List<String> virtues = List.of("courage", "generosity");

        ChatLanguageModel aModel = OllamaChatModel.builder()
                .baseUrl("http://localhost:11444")
                .modelName(virtues.get(0))
                .timeout(Duration.of(5, ChronoUnit.MINUTES))
                .build();

        ChatLanguageModel bModel = OllamaChatModel.builder()
                .baseUrl("http://localhost:11444")
                .modelName(virtues.get(1))
                .timeout(Duration.of(5, ChronoUnit.MINUTES))
                .build();

        String initialPromptTemplate = "You will give only reasoned, logical and philosophically correct answers. You will accept my arguments when they are solid or refute them when they aren't. Discuss why ${virtue1} is a more important human quality than ${virtue2}.";
        Map<String, String> promptAParameters = new HashMap<>();
        promptAParameters.put("virtue1", virtues.get(0));
        promptAParameters.put("virtue2", virtues.get(1));

        Map<String, String> promptBParameters = new HashMap<>();
        promptBParameters.put("virtue1", virtues.get(1));
        promptBParameters.put("virtue2", virtues.get(0));

        String messageTemplate = "${answer} If you agree with me, simply say \"I agree with you\". Otherwise, provide arguments to refute me.";

        String nextMessageA = StringSubstitutor.replace(initialPromptTemplate, promptAParameters);
        String nextMessageB = StringSubstitutor.replace(initialPromptTemplate, promptBParameters);

        boolean finished = false;
        Map<String, String> parameters = new HashMap<>();
        do {
            System.out.println("nextMessageA: " + nextMessageA);
            System.out.println("nextMessageB: " + nextMessageB);

            String answerA = aModel.chat(nextMessageA);
            System.out.println("answerA: " + answerA);
            String answerB = bModel.chat(nextMessageB);
            System.out.println("answerB: " + answerB);

            parameters.put("answer", answerB);
            nextMessageA = StringSubstitutor.replace(messageTemplate, parameters);

            parameters.put("answer", answerA);
            nextMessageB = StringSubstitutor.replace(messageTemplate, parameters);
        } while(!finished);

    }
}
