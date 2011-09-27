package edu.mayo.cts2.sdk.plugin.service.exist.profile.map

import org.springframework.beans.factory.annotation.Autowired

import edu.mayo.cts2.framework.plugin.service.exist.profile.map.ExistMapMaintenanceService;
import edu.mayo.cts2.framework.plugin.service.exist.profile.map.ExistMapReadService;
import edu.mayo.cts2.sdk.model.map.MapCatalogEntry
import edu.mayo.cts2.sdk.model.map.MapCatalogEntrySummary
import edu.mayo.cts2.sdk.model.service.exception.UnknownMap
import edu.mayo.cts2.sdk.model.service.exception.UnknownResourceReference
import edu.mayo.cts2.sdk.plugin.service.exist.profile.BaseServiceTestBaseIT

class ExistMapServiceTestIT extends BaseServiceTestBaseIT[MapCatalogEntry,MapCatalogEntrySummary] {
  
    @Autowired var readService:ExistMapReadService = null
    @Autowired var maintService:ExistMapMaintenanceService = null
 
    def getExceptionClass():Class[_<:UnknownResourceReference] = {
       classOf[UnknownMap]
    }
  
    def createResource(name:String) = {
      var entry = new MapCatalogEntry()
      entry.setMapName(name)
      entry.setAbout("about")
      
      maintService.createResource("", entry);
    }
      
    def getResource(name:String):MapCatalogEntry = {
    	readService.read(name)
    }
  
}