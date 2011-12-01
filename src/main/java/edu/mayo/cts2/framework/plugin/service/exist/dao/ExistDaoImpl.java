package edu.mayo.cts2.framework.plugin.service.exist.dao;

import java.io.IOException;
import java.io.StringWriter;

import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.Marshaller;
import org.springframework.stereotype.Component;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XQueryService;

import edu.mayo.cts2.framework.model.exception.UnspecifiedCts2RuntimeException;
import edu.mayo.cts2.framework.plugin.service.exist.util.ExistServiceUtils;

@Component
public class ExistDaoImpl implements ExistResourceDao {
	
	protected final Log log = LogFactory.getLog(getClass().getName());

	@Autowired
	private ExistManager existManager;

	private static final String XML_RESOURCE_TYPE = "XMLResource";
	
	private static final String BINARY_RESOURCE_TYPE = "BinaryResource";

	private static final String CTS2_RESOURCES_PATH = "/cts2resources";

	@Autowired
	private Marshaller marshaller;
	
	protected void createAndStoreBinaryResource(Object obj,
			Collection collection, String nameWithSuffix) throws XMLDBException {
		Resource resource = collection.createResource(
				ExistServiceUtils.getExistResourceName(nameWithSuffix),
				BINARY_RESOURCE_TYPE);
		
		resource.setContent(obj);

		collection.storeResource(resource);
	}

	protected void createAndStoreResource(Object obj,
			Collection collection, String name) throws XMLDBException {
		Resource resource = collection.createResource(
				ExistServiceUtils.getExistResourceName(name) + ExistServiceUtils.XML_SUFFIX,
				XML_RESOURCE_TYPE);
		
		if(obj instanceof Resource){
			Resource res = (Resource)obj;
			resource.setContent(res.getContent());
		} else if(obj instanceof String){
			resource.setContent((String)obj);
		} else {

			StringWriter sw = new StringWriter();
			StreamResult sr = new StreamResult(sw);
	
			try {
	
				this.marshaller.marshal(obj, sr);
	
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
	
			resource.setContent(sw.toString());
		}

		collection.storeResource(resource);
	}
	
	public void storeResource(String path, String name, Object entry) {
		Collection collection = null;
		try {
			collection = this.getExistManager()
					.getOrCreateCollection(this.getResourcePath(path));

			this.createAndStoreResource(entry, collection, name );

		} catch (XMLDBException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if(collection != null){
					collection.close();
				}
			} catch (XMLDBException e) {
				this.log.warn(e);
			}
		}
	}
	
	public void storeBinaryResource(String path, String name, Object entry) {
		Collection collection = null;
		try {
			collection = this.getExistManager()
					.getOrCreateCollection(this.getResourcePath(path));

			this.createAndStoreBinaryResource(entry, collection, name );

		} catch (XMLDBException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if(collection != null){
					collection.close();
				}
			} catch (XMLDBException e) {
				this.log.warn(e);
			}
		}
	}

	public Resource getResource(String path, String name) {
		Resource resource = this.doGetResource(name, ExistServiceUtils.XML_SUFFIX, this.getResourcePath(path));

		return resource;
	}
	
	public Resource getBinaryResource(String path, String nameWithSuffix) {
		Resource resource = this.doGetResource(nameWithSuffix, "", this.getResourcePath(path));

		return resource;
	}
	
	public void deleteResource(String path, String name) {
		Resource resource = this.doGetResource(name, ExistServiceUtils.XML_SUFFIX, this.getResourcePath(path));
		
		try {
			resource.getParentCollection().removeResource(resource);
		} catch (XMLDBException e) {
			throw new UnspecifiedCts2RuntimeException(e);
		}
	}

	@Override
	public void removeCollection(String collectionPath) {
		
		collectionPath = this.getResourcePath(collectionPath);
		
		try {
			Collection collection =
					this.getExistManager().getOrCreateCollection(collectionPath);
			
			this.getExistManager().getCollectionManagementService().removeCollection(collection.getName());
		} catch (XMLDBException e) {
			throw new UnspecifiedCts2RuntimeException(e);
		}
	}

	public ResourceSet query(
			String collectionPath, 
			String queryString, 
			int start, 
			int max){
		
		try {
			XQueryService xqueryService = this.getExistManager().getXQueryService(getResourcePath(collectionPath));

			return xqueryService.query(queryString);
			
		} catch (XMLDBException e) {
			throw new UnspecifiedCts2RuntimeException(e);
		}
	}
	
	@Override
	public Resource getResourceByXpath(String collectionPath, String xpathQuery) {
		Resource resource;
		try {
		
			ResourceSet resourceSet = this.query(collectionPath, xpathQuery, 0, 1);

			long size = resourceSet.getSize();
			
			if (size == 0) {
				return null;
			}
			
			//this should be caught during insert. If we get this far, the service is in an
			//illegal state.
			if (size > 1) {
				throw new IllegalStateException("Duplicate Entries found.");
			}

			resource = resourceSet.getResource(0);
			
			return resource;
			
		} catch (XMLDBException e) {
			throw new UnspecifiedCts2RuntimeException(e);
		}
	}

	protected String getResourcePath(String path) {
		if(path.contains(CTS2_RESOURCES_PATH)){
			path = StringUtils.substringAfter(path, CTS2_RESOURCES_PATH);
		}
		return CTS2_RESOURCES_PATH + "/" + path;
	}

	protected Resource doGetResource(String name, String suffix, String collection) {
		try {
			return existManager.getOrCreateCollection(collection)
					.getResource(ExistServiceUtils.getExistResourceName(name) + suffix);
		} catch (XMLDBException e) {
			throw new RuntimeException(e);
		}
	}

	protected ExistManager getExistManager() {
		return existManager;
	}

	protected void setExistManager(
			ExistManager existManager) {
		this.existManager = existManager;
	}
}