package br.com.caelum.camel;

import java.text.SimpleDateFormat;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.xstream.XStreamDataFormat;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import com.thoughtworks.xstream.XStream;

import br.com.caelum.camel.pojo.Negociacao;

public class RotaPedidos {

	public static void main(String[] args) throws Exception {
		
		final XStream xstream = new XStream();
		xstream.alias("negociacao", Negociacao.class);
		
		SimpleRegistry registro = new SimpleRegistry();
		registro.put("mysql", criaDataSource());

		CamelContext context = new DefaultCamelContext(registro);
		context.addRoutes(new RouteBuilder() {
			
			
			//File -> File
		/*	@Override
			public void configure() throws Exception {
				from("file:pedidos?delay=5s&noop=true").
				split().
					xpath("/pedido/itens/item").
				filter().
					xpath("/item/formato[text()='EBOOK']").
				log("${id} - ${body}").
				marshal().xmljson().
				log("${body}").
				setHeader("CamelFileName", simple("${file:name.noext}.json")).
				to("file:saida");
			}*/
			 
			
			//FILE -> WS
			/*@Override
			public void configure() throws Exception {
				from("file:pedidos?delay=5s&noop=true").
				log("${body}").
				split()
					.xpath("/pedido/itens/item").
				filter()
					.xpath("/item/formato[text()='EBOOK']").
				log("${id} - ${body}").
				marshal().xmljson().
				log("${body}").
				setHeader(Exchange.HTTP_METHOD, HttpMethods.POST).
				to("http4://localhost:8080/webservices/ebook/item");
			}*/
			
			//WS -> Database
			@Override
			public void configure() throws Exception {
				from("timer://negociacoes?fixedRate=true&delay=1s&period=5s").
			      to("http4://argentumws-spring.herokuapp.com/negociacoes").
			          convertBodyTo(String.class).
			          unmarshal(new XStreamDataFormat(xstream)).
			          split(body()). //cada negociação se torna uma mensagem
			          process(new Processor() {
			              @Override
			              public void process(Exchange exchange) throws Exception {
			                  Negociacao negociacao = exchange.getIn().getBody(Negociacao.class);
			                  exchange.setProperty("preco", negociacao.getPreco());
			                  exchange.setProperty("quantidade", negociacao.getQuantidade());
			                  String data = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(negociacao.getData().getTime());
			                  exchange.setProperty("data", data);
			              }
			            }).
			          setBody(simple("insert into negociacao(preco, quantidade, data) values (${property.preco}, ${property.quantidade}, '${property.data}')")).
			          log("${body}").
			          delay(1000).
			          to("jdbc:mysql");
			}
		});
		
		context.start();
		Thread.sleep(20000);
		context.stop();
	}	
	
	public static MysqlConnectionPoolDataSource criaDataSource() {
	    MysqlConnectionPoolDataSource mysqlDs = new MysqlConnectionPoolDataSource();
	    mysqlDs.setDatabaseName("camel");
	    mysqlDs.setServerName("192.168.99.100");
	    mysqlDs.setPort(3306);
	    mysqlDs.setUser("root");
	    mysqlDs.setPassword("MySql2019!");
	    return mysqlDs;
	}
}
