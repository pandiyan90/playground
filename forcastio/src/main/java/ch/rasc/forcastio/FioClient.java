package ch.rasc.forcastio;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

import ch.rasc.forcastio.json.JacksonJsonConverter;
import ch.rasc.forcastio.json.JsonConverter;
import ch.rasc.forcastio.model.FioBlock;
import ch.rasc.forcastio.model.FioLanguage;
import ch.rasc.forcastio.model.FioRequest;
import ch.rasc.forcastio.model.FioResponse;
import ch.rasc.forcastio.model.FioUnit;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FioClient {
	private final String apiKey;

	private final JsonConverter jsonConverter;

	private final OkHttpClient httpClient;

	private Integer apiCalls;

	private String responseTime;

	public FioClient(String apiKey) {
		this(apiKey, new JacksonJsonConverter(), new OkHttpClient());
	}

	public FioClient(String apiKey, JsonConverter jsonConverter) {
		this(apiKey, jsonConverter, new OkHttpClient());
	}

	public FioClient(String apiKey, JsonConverter jsonConverter,
			OkHttpClient httpClient) {
		this.apiKey = apiKey;
		this.jsonConverter = jsonConverter;
		this.httpClient = httpClient;
	}

	public FioResponse forecastCall(FioRequest request) throws IOException {
		HttpUrl.Builder urlBuilder = new HttpUrl.Builder().scheme("https")
				.host("api.forecast.io").addPathSegment("forecast")
				.addPathSegment(this.apiKey)
				.addPathSegment(request.latitude() + "," + request.longitude());

		if (request.unit() != null && request.unit() != FioUnit.US) {
			urlBuilder.addQueryParameter("units", request.unit().getJsonValue());
		}

		if (request.extendHourly() != null && request.extendHourly().booleanValue()) {
			urlBuilder.addQueryParameter("extend", "hourly");
		}

		if (request.language() != null && request.language() != FioLanguage.EN) {
			urlBuilder.addQueryParameter("lang",
					request.language().name().toLowerCase().replace('_', '-'));
		}

		Set<FioBlock> exclude = EnumSet.noneOf(FioBlock.class);
		
		Set<FioBlock> include = request.includeBlocks();
		if (!include.isEmpty()) {
			for (FioBlock block : FioBlock.values()) {
				if (!include.contains(block)) {
					exclude.add(block);
				}
			}
		}

		if (!request.excludeBlocks().isEmpty()) {
			exclude.addAll(request.excludeBlocks());
			if (exclude.size() == FioBlock.values().length) {
				// Everything excluded
				return null;
			}
			urlBuilder.addQueryParameter("exclude", exclude.stream()
					.map(FioBlock::getJsonValue).collect(Collectors.joining(",")));
		}

		Request getRequest = new Request.Builder().get().url(urlBuilder.build()).build();

		try (Response response = this.httpClient.newCall(getRequest).execute()) {

			String apiCallsString = response.header("X-Forecast-API-Calls");
			if (apiCallsString != null && apiCallsString.trim().length() > 0) {
				this.apiCalls = Integer.valueOf(apiCallsString);
			}

			this.responseTime = response.header("X-Response-Time");

			String jsonData = response.body().string();
			return this.jsonConverter.deserialize(jsonData);
		}
	}

	/**
	 * The number of API calls made for today. Value is only visible after a request
	 */
	public Integer apiCalls() {
		return apiCalls;
	}

	/**
	 * The server-side response time of the last request
	 */
	public String responseTime() {
		return responseTime;
	}

}