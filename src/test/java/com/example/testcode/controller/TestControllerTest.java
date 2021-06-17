package com.example.testcode.controller;

import com.example.testcode.dto.Person;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.restdocs.request.RequestDocumentation;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@TestMethodOrder(OrderAnnotation.class)
@AutoConfigureMockMvc
@SpringBootTest
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
class TestControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private ObjectMapper objectMapper;
	/**
	 * RestDocumentationResultHandler
	 * 문서를 출력하기 위한 Class
	 */
	private RestDocumentationResultHandler document;

	@BeforeEach
	public void setUp(RestDocumentationContextProvider restDocumentation) {
		this.document = document(
				"{class-name}/{method-name}", // 문서 경로 설정
				preprocessRequest( // Request 설정
						modifyUris()
								.scheme("http")
								.host("test.com")
								.port(8080)
								.removePort(), //removePort() 기능이 정확히 뭔지
						// 문서에 노출되는 도메인 설정
						prettyPrint() // 정리해서 출력
				),
				preprocessResponse(prettyPrint()) // Response 설정. 정리해서 출력
		);

//		this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
//				.apply(documentationConfiguration(restDocumentation))
//				.alwaysDo(document("{method-name}", //메소드 이름으로 폴더
//						preprocessRequest(prettyPrint()),
//						preprocessResponse(prettyPrint())))
//				.build();
		//아래와 동일 document의 재사용성을 높이기위해

		this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
				.apply(documentationConfiguration(restDocumentation))
				.alwaysDo(document)
				.build();
	}

	@Test
	@Order(1)
	void getMethod() throws Exception { //이것처럼 할때마다 폴더를 설정하기 보다는 한번 설정하고 계속 꺼내 쓰는게 소요가 적음
		MultiValueMap<String, String> info = new LinkedMultiValueMap<>();
		info.add("name", "한신");
		info.add("id", "123");
		mockMvc.perform(get("/get")
				.params(info)
				.contentType(APPLICATION_JSON_VALUE)
				.accept(APPLICATION_JSON_VALUE))
				.andExpect(status().isOk())
				.andDo(print())
				.andDo(document("sample1233",
						RequestDocumentation.requestParameters(
								parameterWithName("name").description("이름"),
								parameterWithName("id").description("id")
						),
						PayloadDocumentation.responseFields(
								fieldWithPath("name").type("String").description("이름"),
								fieldWithPath("id").type("String").description("id")
						)));
	}

	//Snippet 으로 doc 생성
	@Test
	void GetMethodSnippet() throws Exception {
		MultiValueMap<String, String> info = new LinkedMultiValueMap<>();
		info.add("name", "한신");
		info.add("id", "123");
		mockMvc.perform(get("/get")
				.params(info)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.accept(APPLICATION_JSON_UTF8_VALUE)) //기본 처리가 APPLICATION_JSON_UTF8_VALUE 라고 하면서 실제로는 application/json가 나가서 APPLICATION_JSON_UTF8_VALUE 설정
				.andExpect(status().isOk())
				.andDo(print())
				.andDo(document.document(             // document.document으로 document 설정 바로 가져옴
						RequestDocumentation.requestParameters(
								parameterWithName("name").description("이름"),
								parameterWithName("id").description("id")
						),
						PayloadDocumentation.responseFields(
								fieldWithPath("name").type("String").description("이름"),
								fieldWithPath("id").type("String").description("id")
						)));
	}

	@Test
	@Order(2)
	void postMethod() throws Exception {
		Person person = new Person();
		person.setName("한신");
		person.setAge(14);
		mockMvc.perform(post("/post/{test}", "test")
				.content(objectMapper.writeValueAsString(person)) //추가 값 필요시
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.accept(APPLICATION_JSON_UTF8_VALUE))
				.andDo(print())
				.andExpect(status().isOk())
				.andDo(document.document(
						pathParameters(parameterWithName("test").description("테스트") //path value 로 값 보낼때
						),
						requestFields(
								fieldWithPath("name").type(JsonFieldType.STRING).description("이름"),
								fieldWithPath("age").type(JsonFieldType.NUMBER).description("나이")
						),
//						RequestDocumentation.requestParameters(                   // Parameter 는 쿼리 스트링 GET 에는 Parameter 사용 가능
//								parameterWithName("person.name").description("이름"),  //  field    는  body    POST 처럼 바디에는 field 사용
//								parameterWithName("person.age").description("나이")
//						),
						PayloadDocumentation.responseFields(
								fieldWithPath("test").type("String").description("테스트값"),
								fieldWithPath("value.name").type("String").description("이름"),
								fieldWithPath("value.age").type("int").description("나이")
						)
				));
	}

	@Test
	@Order(3)
	void deleteMethod() throws Exception {
		mockMvc.perform(delete("/delete/{test}", "test")
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.accept(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andDo(print())
				.andExpect(status().isOk())
				.andDo(document.document(
						pathParameters(parameterWithName("test").description("테스트") //path value 로 값 보낼때
						),
						PayloadDocumentation.responseFields(
								fieldWithPath("id").type("String").description("id 값")
						)
				));
	}

	@Test
	@Order(4)
	void putMethod() {
		System.out.println(4);
	}

}