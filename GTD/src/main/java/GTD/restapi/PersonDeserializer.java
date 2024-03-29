/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GTD.restapi;

import GTD.DL.DLEntity.Person;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;

/**
 * 
 * @author simon
 */
public class PersonDeserializer extends JsonDeserializer<Person>
{

	@Override
	public Person deserialize(JsonParser jp, DeserializationContext dc) throws IOException, JsonProcessingException
	{
		JsonNode node = jp.getCodec().readTree(jp);
		String login = node.asText();
		Person person = new Person();
		person.setLogin(login);
		
		return person;
	}
	
}
