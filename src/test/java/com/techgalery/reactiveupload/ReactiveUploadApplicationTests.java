package com.techgalery.reactiveupload;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;

import java.io.File;
import java.time.Duration;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ReactiveUploadApplicationTests {

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void contextLoads() {
	}

	@BeforeEach
	public void setUp() {
		webTestClient = webTestClient
				.mutate()
				.responseTimeout(Duration.ofSeconds(3))
				.build();
	}

	@Test
	@Order(1)
	void reactiveUploadTest() throws Exception {
		File exampleFile = new File(
				this.getClass()
						.getClassLoader()
						.getResource("example.zip")
						.getFile()
		);
		webTestClient.post()
				.uri("/file/upload")
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.body(BodyInserters.fromMultipartData(fromFile(exampleFile)))
				.exchange()
				.expectStatus().isOk()
				.expectBody(String.class)
		;
	}

	public MultiValueMap<String, HttpEntity<?>> fromFile(File file) {
		MultipartBodyBuilder builder = new MultipartBodyBuilder();
		builder.part("file", new FileSystemResource(file));
		return builder.build();
	}
}
