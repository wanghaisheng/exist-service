/*
 * Copyright: (c) 2004-2011 Mayo Foundation for Medical Education and 
 * Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
 * triple-shield Mayo logo are trademarks and service marks of MFMER.
 *
 * Except as contained in the copyright notice above, or as used to identify 
 * MFMER as the author of this software, the trade names, trademarks, service
 * marks, or product names of the copyright holder shall not be used in
 * advertising, promotion or otherwise in connection with this software without
 * prior written authorization of the copyright holder.
 * 
 * Licensed under the Eclipse Public License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * 
 * 		http://www.eclipse.org/legal/epl-v10.html
 * 
 */
package edu.mayo.cts2.framework.plugin.service.exist.profile;

import org.xmldb.api.base.Resource;
import org.xmldb.api.base.XMLDBException;

import edu.mayo.cts2.framework.model.exception.UnspecifiedCts2RuntimeException;
import edu.mayo.cts2.framework.model.service.core.BaseMaintenanceService;
import edu.mayo.cts2.framework.service.profile.UpdateChangeableMetadataRequest;

/**
 * The Class AbstractService.
 *
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
public abstract class AbstractExistMaintenanceService<R,I,T extends BaseMaintenanceService> 
	extends AbstractExistResourceReadingService<R,I,T> implements edu.mayo.cts2.framework.service.profile.BaseMaintenanceService<R,I> {


	
	@Override
	public void updateChangeableMetadata(I identifier,
			UpdateChangeableMetadataRequest request) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteResource(I identifier) {
		Resource resource = this.getResource(identifier);
		try {
			resource.getParentCollection().removeResource(resource);
		} catch (XMLDBException e) {
			throw new UnspecifiedCts2RuntimeException(e);
		}
	}

	@Override
	public void updateResource(R resource) {
		this.createResource(resource);
	}

	@Override
	public void createResource(R resource) {
		String path = 
				this.getResourceInfo().createPathFromResource(resource);
		
		String wholePath = this.createPath(this.getResourceInfo().getResourceBasePath(), path);
		
		String name = this.getResourceInfo().getExistResourceNameFromResource(resource);
		
		this.getExistResourceDao().storeResource(wholePath, name, resource);
	}

}
