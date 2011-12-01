package edu.mayo.cts2.framework.plugin.service.exist.profile.valuesetdefinition;

import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.model.extension.LocalIdValueSetDefinition;
import edu.mayo.cts2.framework.plugin.service.exist.profile.ResourceInfo;
import edu.mayo.cts2.framework.plugin.service.exist.util.ExistServiceUtils;
import edu.mayo.cts2.framework.service.profile.valuesetdefinition.name.ValueSetDefinitionReadId;

@Component
public class ValueSetDefinitionResourceInfo implements ResourceInfo<LocalIdValueSetDefinition,ValueSetDefinitionReadId> {

	private static final String VALUESETDEFINITIONS_PATH = "/valuesetdefinitions";

	@Override
	public String getResourceBasePath() {
		return VALUESETDEFINITIONS_PATH;
	}

	@Override
	public String getResourceXpath() {
		return "/valuesetdefinition:ValueSetDefinition";
	}
	
	@Override
	public boolean isReadByUri(ValueSetDefinitionReadId identifier) {
		return !(identifier.getUri() == null);
	}

	@Override
	public String createPath(ValueSetDefinitionReadId id) {
		return ExistServiceUtils.createPath(id.getValueSet().getName());
	}

	@Override
	public String createPathFromResource(LocalIdValueSetDefinition resource) {
		return ExistServiceUtils.createPath(
				resource.getResource().getDefinedValueSet().getContent());
	}

	@Override
	public String getExistResourceName(ValueSetDefinitionReadId id) {
		return id.getName();
	}

	@Override
	public String getResourceUri(ValueSetDefinitionReadId id) {
		return id.getUri();
	}
	
	@Override
	public String getExistResourceNameFromResource(LocalIdValueSetDefinition resource) {
		return resource.getLocalID();
	}
	
	@Override
	public String getUriXpath() {
		return "@documentURI";
	}

	@Override
	public String getResourceNameXpath() {
		return "@documentURI";
	}
	
}