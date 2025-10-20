package org.furb;

import com.microsoft.azure.cognitiveservices.vision.computervision.*;
import com.microsoft.azure.cognitiveservices.vision.computervision.models.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    // ⚙️ Configurações (substitua pela sua chave e endpoint)
    private static final String KEY = "API_KEY";
    private static final String ENDPOINT = "API_ENDPOINT";

    public static void main(String[] args) {
        System.out.println("🔍 Azure Computer Vision\n");

        ComputerVisionClient client = authenticate(KEY, ENDPOINT);
        String imageUrl = getImageUrlFromUser();

        try {
            analyzeImage(client, imageUrl);
        } catch (Exception e) {
            System.err.println("\n❌ Erro ao analisar a imagem:");
            e.printStackTrace();
        }
    }

    /**
     * Cria o cliente autenticado do serviço
     */
    private static ComputerVisionClient authenticate(String key, String endpoint) {
        return ComputerVisionManager.authenticate(key).withEndpoint(endpoint);
    }

    /**
     * Solicita a URL da imagem ao usuário
     */
    private static String getImageUrlFromUser() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Insira a URL de uma imagem pública: ");
        String imageUrl = scanner.nextLine();
        scanner.close();
        return imageUrl;
    }

    /**
     * Faz a chamada para o serviço e processa a resposta
     */
    private static void analyzeImage(ComputerVisionClient client, String imageUrl) {
        // Quais características queremos extrair da imagem
        List<VisualFeatureTypes> features = new ArrayList<>();
        features.add(VisualFeatureTypes.CATEGORIES);
        features.add(VisualFeatureTypes.TAGS);
        features.add(VisualFeatureTypes.DESCRIPTION);
        features.add(VisualFeatureTypes.OBJECTS);
        features.add(VisualFeatureTypes.COLOR);

        System.out.println("\n🧠 Analisando imagem...");
        ImageAnalysis analysis = client.computerVision().analyzeImage()
                .withUrl(imageUrl)
                .withVisualFeatures(features)
                .execute();

        printAnalysisResults(analysis);
    }

    /**
     * Exibe os resultados no console
     */
    private static void printAnalysisResults(ImageAnalysis analysis) {
        System.out.println("\n📊 RESULTADOS DA ANÁLISE:");
        System.out.println("---------------------------");

        printDescription(analysis);
        printTags(analysis);
        printObjects(analysis);
        printColors(analysis);
    }

    /**
     * Mostra a descrição (legenda) da imagem
     */
    private static void printDescription(ImageAnalysis analysis) {
        if (analysis.description() != null && analysis.description().captions() != null) {
            System.out.println("\n📝 Descrições:");
            for (ImageCaption caption : analysis.description().captions()) {
                System.out.printf(" - \"%s\" (confiança: %.2f)\n", caption.text(), caption.confidence());
            }
        }
    }

    /**
     * Mostra as tags encontradas
     */
    private static void printTags(ImageAnalysis analysis) {
        if (analysis.tags() != null) {
            System.out.println("\n🏷️ Tags detectadas:");
            for (ImageTag tag : analysis.tags()) {
                System.out.printf(" - %s (confiança: %.2f)\n", tag.name(), tag.confidence());
            }
        }
    }

    /**
     * Mostra os objetos detectados
     */
    private static void printObjects(ImageAnalysis analysis) {
        if (analysis.objects() != null) {
            System.out.println("\n📦 Objetos detectados:");
            for (DetectedObject obj : analysis.objects()) {
                System.out.printf(" - %s (%.2f) -> posição [x=%d, y=%d, w=%d, h=%d]\n",
                        obj.objectProperty(),
                        obj.confidence(),
                        obj.rectangle().x(),
                        obj.rectangle().y(),
                        obj.rectangle().w(),
                        obj.rectangle().h());
            }
        }
    }

    /**
     * Mostra cores predominantes
     */
    private static void printColors(ImageAnalysis analysis) {
        if (analysis.color() != null) {
            System.out.println("\n🎨 Informações de cor:");
            System.out.println(" - Cor dominante (fundo): " + analysis.color().dominantColorBackground());
            System.out.println(" - Cor dominante (primeiro plano): " + analysis.color().dominantColorForeground());
            System.out.println(" - Cores dominantes: " + String.join(", ", analysis.color().dominantColors()));
            System.out.println(" - É em tons de preto e branco? " + (analysis.color().isBWImg() ? "Sim" : "Não"));
        }
    }
}
