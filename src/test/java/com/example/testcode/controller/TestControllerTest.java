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
	 * ????????? ???????????? ?????? Class
	 */
	private RestDocumentationResultHandler document;

	@BeforeEach
	public void setUp(RestDocumentationContextProvider restDocumentation) {
		this.document = document(
				"{class-name}/{method-name}", // ?????? ?????? ??????
				preprocessRequest( // Request ??????
						modifyUris()
								.scheme("http")
								.host("test.com")
								.port(8080)
								.removePort(), //removePort() ????????? ????????? ??????
						// ????????? ???????????? ????????? ??????
						prettyPrint() // ???????????? ??????
				),
				preprocessResponse(prettyPrint()) // Response ??????. ???????????? ??????
		);

//		this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
//				.apply(documentationConfiguration(restDocumentation))
//				.alwaysDo(document("{method-name}", //????????? ???????????? ??????
//						preprocessRequest(prettyPrint()),
//						preprocessResponse(prettyPrint())))
//				.build();
		//????????? ?????? document??? ??????????????? ???????????????

		this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
				.apply(documentationConfiguration(restDocumentation))
				.alwaysDo(document)
				.build();
	}

	@Test
	@Order(1)
	void getMethod() throws Exception { //???????????? ???????????? ????????? ???????????? ????????? ?????? ???????????? ?????? ?????? ????????? ????????? ??????
		MultiValueMap<String, String> info = new LinkedMultiValueMap<>();
		info.add("name", "??????");
		info.add("id", "123");
		mockMvc.perform(get("/get")
				.params(info)
				.contentType(APPLICATION_JSON_VALUE)
				.accept(APPLICATION_JSON_VALUE))
				.andExpect(status().isOk())
				.andDo(print())
				.andDo(document("sample1233",
						RequestDocumentation.requestParameters(
								parameterWithName("name").description("??????"),
								parameterWithName("id").description("id")
						),
						PayloadDocumentation.responseFields(
								fieldWithPath("name").type("String").description("??????"),
								fieldWithPath("id").type("String").description("id")
						)));
	}

	//Snippet ?????? doc ??????
	@Test
	void GetMethodSnippet() throws Exception {
		MultiValueMap<String, String> info = new LinkedMultiValueMap<>();
		info.add("name", "??????");
		info.add("id", "123");
		mockMvc.perform(get("/get")
				.params(info)
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.accept(APPLICATION_JSON_UTF8_VALUE)) //?????? ????????? APPLICATION_JSON_UTF8_VALUE ?????? ????????? ???????????? application/json??? ????????? APPLICATION_JSON_UTF8_VALUE ??????
				.andExpect(status().isOk())
				.andDo(print())
				.andDo(document.document(             // document.document?????? document ?????? ?????? ?????????
						RequestDocumentation.requestParameters(
								parameterWithName("name").description("??????"),
								parameterWithName("id").description("id")
						),
						PayloadDocumentation.responseFields(
								fieldWithPath("name").type("String").description("??????"),
								fieldWithPath("id").type("String").description("id")
						)));
	}

	@Test
	@Order(2)
	void postMethod() throws Exception {
		Person person = new Person();
		person.setName("??????");
		person.setAge(14);
		mockMvc.perform(post("/post/{test}", "test")
				.content(objectMapper.writeValueAsString(person)) //?????? ??? ?????????
				.contentType(APPLICATION_JSON_UTF8_VALUE)
				.accept(APPLICATION_JSON_UTF8_VALUE))
				.andDo(print())
				.andExpect(status().isOk())
				.andDo(document.document(
						pathParameters(parameterWithName("test").description("?????????") //path value ??? ??? ?????????
						),
						requestFields(
								fieldWithPath("name").type(JsonFieldType.STRING).description("??????"),
								fieldWithPath("age").type(JsonFieldType.NUMBER).description("??????")
						),
//						RequestDocumentation.requestParameters(                   // Parameter ??? ?????? ????????? GET ?????? Parameter ?????? ??????
//								parameterWithName("person.name").description("??????"),  //  field    ???  body    POST ?????? ???????????? field ??????
//								parameterWithName("person.age").description("??????")
//						),
						PayloadDocumentation.responseFields(
								fieldWithPath("test").type("String").description("????????????"),
								fieldWithPath("value.name").type("String").description("??????"),
								fieldWithPath("value.age").type("int").description("??????")
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
						pathParameters(parameterWithName("test").description("?????????") //path value ??? ??? ?????????
						),
						PayloadDocumentation.responseFields(
								fieldWithPath("id").type("String").description("id ???")
						)
				));
	}

	@Test
	@Order(4)
	void putMethod() {
		System.out.println(4);
	}

}