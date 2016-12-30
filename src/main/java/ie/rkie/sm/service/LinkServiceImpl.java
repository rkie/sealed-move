package ie.rkie.sm.service;

import org.springframework.stereotype.Service;

@Service
public class LinkServiceImpl implements LinkService {

	@Override
	public String baseUrl(String host, int port) {
		String portStr = "";
		if ( port != 80 ) {
			portStr = ":" + port;
		}
		String url = "http://" + host + portStr + "/";
		return url;
	}

}
