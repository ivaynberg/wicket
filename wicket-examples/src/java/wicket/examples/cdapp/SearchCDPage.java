/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package wicket.examples.cdapp;

import java.util.List;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.Transaction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import wicket.IFeedback;
import wicket.PageParameters;
import wicket.RequestCycle;
import wicket.WicketRuntimeException;
import wicket.contrib.data.util.hibernate.HibernateHelper;
import wicket.examples.WicketExamplePage;
import wicket.examples.cdapp.model.CD;
import wicket.markup.html.WebMarkupContainer;
import wicket.markup.html.basic.Label;
import wicket.markup.html.form.Form;
import wicket.markup.html.form.TextField;
import wicket.markup.html.link.Link;
import wicket.markup.html.list.ListItem;
import wicket.markup.html.list.PageableListView;
import wicket.markup.html.list.PageableListViewNavigation;
import wicket.markup.html.list.PageableListViewNavigationLink;
import wicket.markup.html.panel.FeedbackPanel;
import wicket.model.IModel;


/**
 * Page that nests a search form and a pageable and sortable results table.
 * 
 * @author Eelco Hillenius
 */
public class SearchCDPage extends WicketExamplePage
{
	/** Logger. */
	private static Log log = LogFactory.getLog(SearchCDPage.class);

	/** list view for search results. */
	private SearchCDResultsListView resultsListView;

	/** search form. */
	private final SearchForm searchForm;

	/** model for searching. */
	private final CDSearchModel searchModel;

	/**
	 * Refers to a possible message set from the outside (details page).
	 * To be checked on each rendering: when there is a message, add it to the current
	 * queue and set this variable to null.
	 */
	private String infoMessageForNextRendering = null;

	/**
	 * Construct.
	 */
	public SearchCDPage()
	{
		this(null);
	}

	/**
	 * Construct.
	 * @param pageParameters parameters for this page
	 */
	public SearchCDPage(PageParameters pageParameters)
	{
		super();
		final int rowsPerPage = 8;
		searchModel = new CDSearchModel(rowsPerPage);
		FeedbackPanel feedback = new FeedbackPanel("feedback");
		searchForm = new SearchForm("searchForm", feedback);
		add(searchForm);
		add(feedback);
		resultsListView = new SearchCDResultsListView("results", searchModel, rowsPerPage);
		add(resultsListView);
		WebMarkupContainer resultsTableHeader = new WebMarkupContainer("resultsHeader")
		{
			public boolean isVisible()
			{
				return searchModel.hasResults();
			}
		};
		resultsTableHeader.add(new SortLink("sortOnArtist", "performers"));
		resultsTableHeader.add(new SortLink("sortOnTitle", "title"));
		resultsTableHeader.add(new SortLink("sortOnYear", "year"));
		resultsTableHeader.add(new SortLink("sortOnLabel", "label"));
		resultsTableHeader.setVisible(false); // non-visible as there are no
		// results yet
		add(resultsTableHeader);
		add(new DetailLink("newCdLink", null)); // add with null; the model and
			// the detail page are smart enough to know we want a new one then
		add(new CDTableNavigation("navigation", resultsListView));
	}

	/**
	 * @see wicket.Component#modelChangedStructure()
	 */
	public void modelChangedStructure()
	{
		resultsListView.modelChangedStructure(); // let list view re-populate
		super.modelChangedStructure();
	}

	/**
	 * Sets the result page to the first page.
	 */
	public void setCurrentResultPageToFirst()
	{
		resultsListView.setCurrentPage(0);
	}

	/**
	 * Sets a message for next rendering.
	 * @param externalMessage message set from the outside (details page).
	 * 		To be checked on each rendering: when there is a message, add it to
	 * 		the current queue and set this variable to null
	 */
	public final void setInfoMessageForNextRendering(String externalMessage)
	{
		this.infoMessageForNextRendering = externalMessage;
	}

	protected void onRender()
	{
		if (infoMessageForNextRendering != null)
		{
			searchForm.setMessage(infoMessageForNextRendering);
			infoMessageForNextRendering = null;
		}
		super.onRender();
	}

	/**
	 * Gets the current number of results.
	 * @return the current number of results
	 */
	private int getNumberOfResults()
	{
		return ((List)resultsListView.getModelObject()).size();
	}

	/**
	 * Form for search actions.
	 */
	private class SearchForm extends Form
	{
		/** search property to set. */
		private String search;

		/**
		 * Constructor
		 * 
		 * @param componentName Name of the form component
		 * @param errorHandler the error handler
		 */
		public SearchForm(final String componentName, final IFeedback errorHandler)
		{
			super(componentName, errorHandler);
			add(new TextField("search", this, "search"));
		}

		/**
		 * @see wicket.markup.html.form.Form#onSubmit()
		 */
		public final void onSubmit()
		{
			searchModel.setSearchString(search); // set search query on model
			setCurrentResultPageToFirst(); // start with first page
			SearchCDPage.this.modelChangedStructure();

			if(search != null && (!search.trim().equals("")))
			{
				info(getNumberOfResults() + " results found for query '" + search + "'");
			}
		}

		/**
		 * Gets search property.
		 * @return search property
		 */
		public final String getSearch()
		{
			return search;
		}

		/**
		 * Sets search property.
		 * @param search search property
		 */
		public final void setSearch(String search)
		{
			this.search = search;
		}

		/** hack to get message displayed. */
		void setMessage(String msg)
		{
			get("search").info(msg);
		}
	}

	/**
	 * Table for displaying search results.
	 */
	private class SearchCDResultsListView extends PageableListView
	{
		/**
		 * Construct.
		 * 
		 * @param componentName name of the component
		 * @param model the model
		 * @param pageSizeInCells page size
		 */
		public SearchCDResultsListView(String componentName,
				IModel model, int pageSizeInCells)
		{
			super(componentName, model, pageSizeInCells);
		}

		/**
		 * @see wicket.markup.html.list.ListView#populateItem(wicket.markup.html.list.ListItem)
		 */
		public void populateItem(final ListItem item)
		{
			final CD cd = (CD)item.getModelObject();
			final Long id = cd.getId();

			// add links to the details
			item.add(new DetailLink("title", id)
					.add(new Label("title", cd.getTitle())));
			item.add(new DetailLink("performers", id)
					.add(new Label("performers", cd.getPerformers())));
			item.add(new DetailLink("label", id)
					.add(new Label("label", cd.getLabel())));
			item.add(new DetailLink("year", id)
					.add(new Label("year", (cd.getYear() != null) ? cd.getYear().toString() : "")));

			// add a delete link for each found record
			item.add(new DeleteLink("delete", id));
		}

		/**
		 * @see wicket.markup.html.list.ListView#modelChangedStructure()
		 */
		public void modelChangedStructure()
		{
			getModel().detach(); // force reload right away
			super.modelChangedStructure();
		}
	}

	/** link to detail edit page. */
	private final class DetailLink extends Link
	{
		/**
		 * Construct.
		 * @param name name of the component
		 * @param id the id of the cd
		 */
		public DetailLink(String name, Long id)
		{
			super(name, id);
		}

		/**
		 * @see wicket.markup.html.link.Link#onClick()
		 */
		public void onClick()
		{
			final RequestCycle requestCycle = getRequestCycle();
			final Long id = (Long)getModelObject();
			requestCycle.setPage(new EditCDPage(SearchCDPage.this, id));
		}	
	}

	/** Link for deleting a row. */
	private final class DeleteLink extends Link
	{
		/**
		 * Construct.
		 * @param name name of the component
		 * @param id the id of the cd
		 */
		public DeleteLink(String name, Long id)
		{
			super(name, id);
		}

		/**
		 * @see wicket.markup.html.link.Link#onClick()
		 */
		public void onClick()
		{
			final Long id = (Long)getModelObject();
			Session session = null;
			Transaction tx = null;
			try
			{
				session = HibernateHelper.getSession();
				tx = session.beginTransaction();
				CD toDelete = (CD)session.load(CD.class, id);
				session.delete(toDelete);
				tx.commit();
				info(" cd " + toDelete.getTitle() + " deleted");
				SearchCDPage.this.modelChangedStructure();
			}
			catch (HibernateException e)
			{
				try
				{
					tx.rollback();
				}
				catch (HibernateException ex)
				{
				}
				throw new WicketRuntimeException(e);
			}
		}
	}

	/** Link for sorting on a column. */
	private final class SortLink extends Link
	{
		/** order by field. */
		private final String field;

		/**
		 * Construct.
		 * 
		 * @param componentName name of component
		 * @param field order by field
		 */
		public SortLink(String componentName, String field)
		{
			super(componentName);
			this.field = field;
		}

		/**
		 * Add order by field to query of list.
		 * 
		 * @see wicket.markup.html.link.Link#onClick()
		 */
		public void onClick()
		{
			searchModel.addOrdering(field);
			SearchCDPage.this.modelChangedStructure();
		}
	}

	/**
	 * Custom table navigation class that adds extra labels.
	 */
	private static class CDTableNavigation extends PageableListViewNavigation
	{
		/**
		 * Construct.
		 * 
		 * @param componentName the name of the component
		 * @param table the table
		 */
		public CDTableNavigation(String componentName, PageableListView table)
		{
			super(componentName, table);
		}

		/**
		 * @see wicket.markup.html.list.ListView#populateItem(wicket.markup.html.list.ListItem)
		 */
		protected void populateItem(ListItem item)
		{
			final int page = ((Integer)item.getModelObject()).intValue();
			final PageableListViewNavigationLink link = new PageableListViewNavigationLink("pageLink",
					pageableListView, page);

			if (page > 0)
			{
				item.add(new Label("separator", "|"));
			}
			else
			{
				item.add(new Label("separator", ""));
			}
			link.add(new Label("pageNumber", String.valueOf(page + 1)));
			link.add(new Label("pageLabel", "page"));
			item.add(link);
		}
	}
}