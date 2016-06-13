package easy.data.xml;

import easy.data.util.XML;

public class IdentityProcessor implements IPostProcessor {

	public IdentityProcessor() {}

	public XML process(XML xml) {
		System.out.println("! Identity processor !");
		return xml;
	}

}
