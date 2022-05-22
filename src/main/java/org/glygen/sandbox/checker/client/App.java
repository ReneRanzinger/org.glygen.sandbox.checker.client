package org.glygen.sandbox.checker.client;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.glygen.sandbox.checker.client.http.APIClient;
import org.glygen.sandbox.checker.client.http.WebResponse;

public class App
{
    public static void main(String[] a_args) throws IOException
    {
        APIClient t_client = new APIClient("https://glygen.ccrc.uga.edu/sandbox/api/checkers.php");

        Path t_filePath = Path.of("glycan.glycoct");

        String t_glycoCT = Files.readString(t_filePath);
        t_glycoCT = t_glycoCT.replaceAll("\n", " ");
        t_glycoCT = t_glycoCT.replaceAll("\r", "");

        WebResponse t_response = t_client.checkGlycanGet(t_glycoCT, "O", false, false, false);

        System.out.println("\nHTTP Code: " + t_response.getHttpCode());
        System.out.println(t_response.getContent());
    }
}
