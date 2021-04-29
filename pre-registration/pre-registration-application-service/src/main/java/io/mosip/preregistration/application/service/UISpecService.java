package io.mosip.preregistration.application.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.preregistration.application.dto.PageDTO;
import io.mosip.preregistration.application.dto.UISpecDTO;
import io.mosip.preregistration.application.dto.UISpecMetaDataDTO;
import io.mosip.preregistration.application.dto.UISpecResponseDTO;
import io.mosip.preregistration.application.dto.UISpecficationRequestDTO;
import io.mosip.preregistration.application.exception.UISpecException;
import io.mosip.preregistration.application.service.util.UISpecServiceUtil;
import io.mosip.preregistration.core.common.dto.ExceptionJSONInfoDTO;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.config.LoggerConfiguration;

@Service
public class UISpecService {

	@Value("${version}")
	private String version;

	@Autowired
	UISpecServiceUtil serviceUtil;

	/**
	 * Logger instance
	 */
	private Logger log = LoggerConfiguration.logConfig(UISpecService.class);

	private final String domain = "pre-registration";

	public MainResponseDTO<UISpecResponseDTO> saveUIspec(UISpecDTO request) {
		log.info("In UISpec service saveUIspec method");
		MainResponseDTO<UISpecResponseDTO> response = new MainResponseDTO<UISpecResponseDTO>();
		response.setVersion(version);
		response.setResponsetime(LocalDateTime.now().toString());
		UISpecResponseDTO uispecReq = new UISpecResponseDTO();
		try {
			log.info("Saving the UiSpec request {}", request);
			uispecReq = serviceUtil.saveUISchema(getMasterDataUISpecRequest(request));
			response.setResponse(uispecReq);
		} catch (UISpecException ex) {
			log.error("Exception occured while saving the UiSpec request {}", request);
			List<ExceptionJSONInfoDTO> explist = new ArrayList<ExceptionJSONInfoDTO>();
			ExceptionJSONInfoDTO exception = new ExceptionJSONInfoDTO();
			exception.setErrorCode(ex.getErrorCode());
			exception.setMessage(ex.getMessage());
			log.error("Exception {}", exception);
			explist.add(exception);
			response.setErrors(explist);
		}
		return response;
	}

	public MainResponseDTO<UISpecResponseDTO> updateUISpec(UISpecDTO updateRequest, String id) {
		log.info("In UISpec service updateUIspec method");
		MainResponseDTO<UISpecResponseDTO> response = new MainResponseDTO<UISpecResponseDTO>();
		response.setVersion(version);
		response.setResponsetime(LocalDateTime.now().toString());
		UISpecResponseDTO uispecResponse = new UISpecResponseDTO();
		try {
			log.info("updating the UiSpec request {}", updateRequest);
			uispecResponse = serviceUtil.updateUISchema(getMasterDataUISpecRequest(updateRequest), id);
			response.setResponse(uispecResponse);
		} catch (UISpecException ex) {
			log.error("Exception occured while updating the UiSpec request {}", updateRequest);
			List<ExceptionJSONInfoDTO> explist = new ArrayList<ExceptionJSONInfoDTO>();
			ExceptionJSONInfoDTO exception = new ExceptionJSONInfoDTO();
			exception.setErrorCode(ex.getErrorCode());
			exception.setMessage(ex.getMessage());
			log.error("Exception {}", exception);
			explist.add(exception);
			response.setErrors(explist);
		}
		return response;
	}

	public MainResponseDTO<List<UISpecMetaDataDTO>> getUISpec(double version, double identitySchemaVersion) {
		log.info("In UISpec service getUIspec method");
		MainResponseDTO<List<UISpecMetaDataDTO>> response = new MainResponseDTO<List<UISpecMetaDataDTO>>();
		response.setVersion(this.version);
		response.setResponsetime(LocalDateTime.now().toString());
		try {
			log.info("fetching the UiSpec version {} and identitySchemaVersion {}", version, identitySchemaVersion);
			List<UISpecResponseDTO> uiSchema = serviceUtil.getUISchema(version, identitySchemaVersion);
			List<UISpecMetaDataDTO> fetchedSchema = prepareResponse(uiSchema);
			if (identitySchemaVersion == 0) {
				ArrayList<UISpecMetaDataDTO> latestPublishedList = new ArrayList<>();
				latestPublishedList.add(getLatestPublishedSchema(fetchedSchema));
				response.setResponse(latestPublishedList);
			} else {
				response.setResponse(fetchedSchema);
			}

		} catch (UISpecException ex) {
			log.error("Exception occured while fetching the UiSpec");
			List<ExceptionJSONInfoDTO> explist = new ArrayList<ExceptionJSONInfoDTO>();
			ExceptionJSONInfoDTO exception = new ExceptionJSONInfoDTO();
			exception.setErrorCode(ex.getErrorCode());
			exception.setMessage(ex.getMessage());
			log.error("Exception {}", exception);
			explist.add(exception);
			response.setErrors(explist);
		}
		return response;
	}

	private UISpecficationRequestDTO getMasterDataUISpecRequest(UISpecDTO request) {
		UISpecficationRequestDTO masterDataRequest = new UISpecficationRequestDTO();
		masterDataRequest.setDomain(domain);
		masterDataRequest.setDescription(request.getDescription());
		masterDataRequest.setIdentitySchemaId(request.getIdentitySchemaId());
		masterDataRequest.setTitle(request.getTitle());
		masterDataRequest.setType(request.getType());
		masterDataRequest.setJsonspec(request.getJsonspec());

		return masterDataRequest;
	}

	public MainResponseDTO<String> deleteUISpec(String id) {
		log.info("In UISpec service deleteUISpec method");
		MainResponseDTO<String> response = new MainResponseDTO<String>();
		response.setVersion(this.version);
		response.setResponsetime(LocalDateTime.now().toString());
		try {
			log.info("deleting the UiSpec id {}", id);
			response.setResponse(serviceUtil.deleteUISchema(id));
		} catch (UISpecException ex) {
			log.error("Exception occured while fetching the UiSpec");
			List<ExceptionJSONInfoDTO> explist = new ArrayList<ExceptionJSONInfoDTO>();
			ExceptionJSONInfoDTO exception = new ExceptionJSONInfoDTO();
			exception.setErrorCode(ex.getErrorCode());
			exception.setMessage(ex.getMessage());
			log.error("Exception {}", exception);
			explist.add(exception);
			response.setErrors(explist);
		}
		return response;
	}

	public MainResponseDTO<String> publishUISpec(String id) {
		log.info("In UISpec service publishUIspec method");
		MainResponseDTO<String> response = new MainResponseDTO<String>();
		response.setVersion(version);
		response.setResponsetime(LocalDateTime.now().toString());
		try {
			log.info("publish the UiSpec request id {}", id);
			response.setResponse(serviceUtil.publishUISchema(id));
		} catch (UISpecException ex) {
			log.error("Exception occured while publishing the UiSpec id {}", id);
			List<ExceptionJSONInfoDTO> explist = new ArrayList<ExceptionJSONInfoDTO>();
			ExceptionJSONInfoDTO exception = new ExceptionJSONInfoDTO();
			exception.setErrorCode(ex.getErrorCode());
			exception.setMessage(ex.getMessage());
			log.error("Exception {}", exception);
			explist.add(exception);
			response.setErrors(explist);
		}
		return response;
	}

	public MainResponseDTO<PageDTO<UISpecMetaDataDTO>> getAllUISpec(int pageNumber, int pageSize) {
		log.info("In UISpec service getAllUISpec method");
		MainResponseDTO<PageDTO<UISpecMetaDataDTO>> response = new MainResponseDTO<PageDTO<UISpecMetaDataDTO>>();
		response.setVersion(this.version);
		response.setResponsetime(LocalDateTime.now().toString());
		try {
			log.info("fetching the All published UiSpec");
			PageDTO<UISpecResponseDTO> res = serviceUtil.getAllUISchema(pageNumber, pageSize);
			PageDTO<UISpecMetaDataDTO> fetchedSchema = new PageDTO<UISpecMetaDataDTO>();
			fetchedSchema.setData(filterPreRegSpec(res.getData()));
			fetchedSchema.setPageNo(res.getPageNo());
			fetchedSchema.setPageSize(res.getPageSize());
			fetchedSchema.setTotalItems(fetchedSchema.getData().size());
			fetchedSchema.setTotalPages(res.getTotalPages());
			fetchedSchema.setSort(res.getSort());
			response.setResponse(fetchedSchema);
		} catch (UISpecException ex) {
			log.error("Exception occured while fetching all the UiSpec");
			List<ExceptionJSONInfoDTO> explist = new ArrayList<ExceptionJSONInfoDTO>();
			ExceptionJSONInfoDTO exception = new ExceptionJSONInfoDTO();
			exception.setErrorCode(ex.getErrorCode());
			exception.setMessage(ex.getMessage());
			log.error("Exception {}", exception);
			explist.add(exception);
			response.setErrors(explist);
		}
		return response;
	}

	private List<UISpecMetaDataDTO> prepareResponse(List<UISpecResponseDTO> uiSchema) {
		List<UISpecMetaDataDTO> res = new ArrayList<>();
		uiSchema.forEach(spec -> {
			System.out.println(spec.getId());
			UISpecMetaDataDTO specData = new UISpecMetaDataDTO();
			specData.setId(spec.getId());
			specData.setDescription(spec.getDescription());
			specData.setVersion(spec.getVersion());
			specData.setIdentitySchemaId(spec.getIdentitySchemaId());
			specData.setIdSchemaVersion(spec.getIdSchemaVersion());
			specData.setTitle(spec.getTitle());
			specData.setEffectiveFrom(spec.getEffectiveFrom());
			specData.setStatus(spec.getStatus());
			specData.setCreatedOn(spec.getCreatedOn());
			specData.setUpdatedOn(spec.getUpdatedOn());
			specData.setJsonSpec(spec.getJsonSpec().get(0).getSpec());
			res.add(specData);
		});
		return res;
	}

	private UISpecMetaDataDTO getLatestPublishedSchema(List<UISpecMetaDataDTO> fetchedSchema) {
		List<UISpecMetaDataDTO> sorted = fetchedSchema.stream().filter(spec -> spec.getStatus().equals("PUBLISHED"))
				.sorted(Comparator.comparing(UISpecMetaDataDTO::getEffectiveFrom).reversed())
				.collect(Collectors.toList());
		sorted.forEach(spec -> System.out.println(spec.getEffectiveFrom()));
		return sorted.get(0);
	}

	private List<UISpecMetaDataDTO> filterPreRegSpec(List<UISpecResponseDTO> data) {
		List<UISpecResponseDTO> filteredData = new ArrayList<UISpecResponseDTO>();
		data.forEach(spec -> {
			if (spec.getDomain().equals(domain)) {
				filteredData.add(spec);
			}
		});
		return prepareResponse(filteredData);
	}

}