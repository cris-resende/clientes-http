package br.edu.infnet.java;

import br.edu.infnet.java.model.Mensalista;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class ClienteApp {

    private static final String BASE_URL = "http://localhost:7000";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) {
        try {
            System.out.println("--- Cliente para /status ---");
            getClientStatus();

            System.out.println("\n--- Cliente para POST /mensalistas (Criar) ---");
            Mensalista novoMensalista = new Mensalista("M006", "Gabriela Dias", "Gerente de Projetos", 8500.0);
            postMensalista(novoMensalista);

            System.out.println("\n--- Cliente para GET /mensalistas ---");
            getTodosMensalistas();

            System.out.println("\n--- Cliente para GET /mensalistas/{matricula} ---");
            getMensalistaPorMatricula("M001");
            getMensalistaPorMatricula("M999"); // Testar um que não existe

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void getClientStatus() throws Exception {
        URL url = new URL(BASE_URL + "/status");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        System.out.println("Status da Resposta: " + responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        connection.disconnect();

        System.out.println("Resposta: " + content.toString());
    }

    private static void postMensalista(Mensalista mensalista) throws Exception {
        URL url = new URL(BASE_URL + "/mensalistas");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        String jsonInputString = objectMapper.writeValueAsString(mensalista);

        try(OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int responseCode = connection.getResponseCode();
        System.out.println("Status da Resposta para POST Mensalista: " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_CREATED) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = in.readLine()) != null) {
                response.append(responseLine.trim());
            }
            in.close();
            System.out.println("Mensalista criado: " + response.toString());
        } else {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getErrorStream(), "utf-8"));
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = in.readLine()) != null) {
                response.append(responseLine.trim());
            }
            in.close();
            System.out.println("Erro ao criar mensalista: " + response.toString());
        }
        connection.disconnect();
    }

    private static void getTodosMensalistas() throws Exception {
        URL url = new URL(BASE_URL + "/mensalistas");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        System.out.println("Status da Resposta para GET Todos Mensalistas: " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            Mensalista[] mensalistasArray = objectMapper.readValue(content.toString(), Mensalista[].class);
            List<Mensalista> mensalistas = Arrays.asList(mensalistasArray);
            System.out.println("Lista de Mensalistas:");
            mensalistas.forEach(System.out::println);
        } else {
            System.out.println("Erro ao listar mensalistas. Código: " + responseCode);
        }
        connection.disconnect();
    }

    private static void getMensalistaPorMatricula(String matricula) throws Exception {
        URL url = new URL(BASE_URL + "/mensalistas/" + matricula);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        System.out.println("Status da Resposta para GET Mensalista por Matrícula '" + matricula + "': " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            Mensalista mensalista = objectMapper.readValue(content.toString(), Mensalista.class);
            System.out.println("Mensalista encontrado: " + mensalista);
        } else {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            System.out.println("Erro ao buscar mensalista: " + content.toString());
        }
        connection.disconnect();
    }
}