package edu.mayo.cts2.framework.plugin.service.exist.profile.mapentry;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.plugin.service.exist.dao.MapEntryExistDao;
import edu.mayo.cts2.framework.plugin.service.exist.profile.AbstractExistQueryService;
import edu.mayo.cts2.framework.plugin.service.exist.restrict.directory.XpathDirectoryBuilder;
import edu.mayo.cts2.framework.plugin.service.exist.xpath.XpathStateBuildingRestriction;
import edu.mayo.cts2.framework.plugin.service.exist.xpath.XpathStateUpdater;
import edu.mayo.cts2.framework.plugin.service.exist.xpath.XpathStateBuildingRestriction.AllOrAny;
import edu.mayo.cts2.sdk.filter.match.StateAdjustingModelAttributeReference.StateUpdater;
import edu.mayo.cts2.sdk.model.core.FilterComponent;
import edu.mayo.cts2.sdk.model.core.PredicateReference;
import edu.mayo.cts2.sdk.model.directory.DirectoryResult;
import edu.mayo.cts2.sdk.model.mapversion.MapEntry;
import edu.mayo.cts2.sdk.model.mapversion.MapEntryDirectoryEntry;
import edu.mayo.cts2.sdk.model.service.core.Query;
import edu.mayo.cts2.sdk.service.command.Page;
import edu.mayo.cts2.sdk.service.command.restriction.MapEntryQueryServiceRestrictions;
import edu.mayo.cts2.sdk.service.profile.mapentry.MapEntryQueryService;

@Component
public class ExistMapEntryQueryService
	extends AbstractExistQueryService
		<edu.mayo.cts2.sdk.model.service.mapentry.MapEntryQueryService,MapEntryDirectoryState>
	implements MapEntryQueryService {

	@Resource
	private MapEntryExistDao mapEntryExistDao;

	private class MapEntryDirectoryBuilder extends
			XpathDirectoryBuilder<MapEntryDirectoryState,MapEntryDirectoryEntry> {

		public MapEntryDirectoryBuilder restrict(
				final MapEntryQueryServiceRestrictions restrictions) {

			if (restrictions != null) {
				if(StringUtils.isNotBlank(restrictions.getMapversion())){
					this.getRestrictions().add(new StateBuildingRestriction<MapEntryDirectoryState>() {

						@Override
						public MapEntryDirectoryState restrict(
								MapEntryDirectoryState state) {
							state.setMapVersion(restrictions.getMapversion());
							return state;
						}
					});
				}
				if(CollectionUtils.isNotEmpty(restrictions.getTargetentity())) {
					
					this.getRestrictions().add(
						 new XpathStateBuildingRestriction<MapEntryDirectoryState>(
								"/mapversion:MapEntry", 
								"mapversion:mapSet/mapversion:mapTarget/mapversion:mapTo/core:name", 
								"text()",
								AllOrAny.ANY,
								restrictions.getTargetentity()));
				}
			}
			return this;
		}

		public MapEntryDirectoryBuilder() {
			super(new MapEntryDirectoryState(),
					new Callback<MapEntryDirectoryState, MapEntryDirectoryEntry>() {

				@Override
				public DirectoryResult<MapEntryDirectoryEntry> execute(
						MapEntryDirectoryState state, int start, int maxResults) {
					return mapEntryExistDao.getResourceSummaries(
							createPath(state.getMapVersion()), 
							state.getXpath(), 
							start,
							maxResults);
				}

				@Override
				public int executeCount(MapEntryDirectoryState state) {
					throw new UnsupportedOperationException();
				}
			}, 
			getAvailableMatchAlgorithmReferences(), 
			getAvailableModelAttributeReferences());
		}
	}

	@Override
	public DirectoryResult<MapEntryDirectoryEntry> getResourceSummaries(
			Query query, 
			FilterComponent filterComponent,
			MapEntryQueryServiceRestrictions restrictions, 
			Page page) {
		MapEntryDirectoryBuilder builder = new MapEntryDirectoryBuilder();

		return builder.
				restrict(restrictions).
				restrict(filterComponent).
				restrict(query).
				addMaxToReturn(page.getEnd()).
				addStart(page.getStart()).
				resolve();
	}

	@Override
	public DirectoryResult<MapEntry> getResourceList(Query query,
			FilterComponent filterComponent,
			MapEntryQueryServiceRestrictions restrictions, Page page) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int count(Query query, FilterComponent filterComponent,
			MapEntryQueryServiceRestrictions restrictions) {
		throw new UnsupportedOperationException();
	}

	@Override
	public PredicateReference getPropertyReference(String nameOrUri) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	protected List<? extends PredicateReference> getAvailablePredicateReferences() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected StateUpdater<MapEntryDirectoryState> getResourceNameStateUpdater() {
		return new XpathStateUpdater<MapEntryDirectoryState>("/mapversion:MapEntry","mapversion:mapFrom/core:name","text()");
	}
}
