import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes=com.example.demo.controller.PackageController.class)
public class PackageControllerTest
{
    @Test
    public void packageDoesNotExists_404IsReceived() throws Exception
    {
        // Given
        String name = "asdf";

        HttpUriRequest request = new HttpGet("http://localhost:8080/api/packages/:" + name);

        HttpResponse response = HttpClientBuilder.create().build().execute(request);

        Assertions.assertEquals(response.getStatusLine().getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }

    @Test
    public void
    packageExists_retrievedResourceIsCorrect() throws Exception
    {
        String name = "sudo";

        HttpUriRequest request = new HttpGet("http://localhost:8080/api/packages/:" + name);

        HttpResponse response = HttpClientBuilder.create().build().execute(request);

        Assertions.assertEquals(response.getStatusLine().getStatusCode(), HttpStatus.SC_OK);

        // test output here if needed
    }
}
