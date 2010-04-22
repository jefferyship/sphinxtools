package org.sphx.test;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.sphx.api.SphinxClient;
import org.sphx.api.SphinxException;
import org.sphx.api.SphinxMatch;
import org.sphx.api.SphinxResult;
import org.sphx.api.SphinxWordInfo;

/**
 * Test class for sphinx API
 */
public class test
{
	public static void main ( String[] argv ) throws SphinxException
	{
//		SQLUtils.init();
		StringBuffer q = new StringBuffer();
//		String host = "202.173.255.59";
		String host = "192.168.1.111";
		int port = 3012;	//培训搜索
//		int port = 3014;	//博客搜索
//		int port = 3013;	//图书搜索
//		int mode = SphinxClient.SPH_MATCH_ALL;
		int mode = SphinxClient.SPH_MATCH_EXTENDED;
		String index = "main";
		int offset = 0;
		int limit = 20;
//		int sortMode = SphinxClient.SPH_SORT_EXPR;
		int sortMode = SphinxClient.SPH_SORT_RELEVANCE;
		String sortClause = "";
//		String groupBy = "ET_CostID";
//		String groupBy = "ET_DomainID";
		String groupBy = "";
//		String groupBy = "MB_DomainID";
		String groupSort = "@count desc";
		
		SphinxClient cl = new SphinxClient();

		cl.SetServer ( host, port );
		cl.SetWeights ( new int[] { 100, 1 } );
		cl.SetMatchMode ( mode );
		cl.SetLimits ( offset, limit );
		
		cl.SetSortMode ( sortMode, sortClause );
		if ( groupBy.length()>0 ){			
			cl.SetGroupBy ( groupBy, SphinxClient.SPH_GROUPBY_ATTR, groupSort );
		}

		q.append("@ET_TypeVar \"市场营销\"");
//		q.append("@ET_DomainID 1");
//		q.append("@MB_Title 高考");
//		q.append("@groupIdVar 3");
//		q.append("@MB_NewsTime 20091203");
//		q.append("34 ");
		SphinxResult res = cl.Query(q.toString(), index);
		
		if ( res==null )
		{
			System.err.println ( "Error: " + cl.GetLastError() );
			System.exit ( 1 );
		}
		if ( cl.GetLastWarning()!=null && cl.GetLastWarning().length()>0 )
			System.out.println ( "WARNING: " + cl.GetLastWarning() + "\n" );

		/* print me out */
		System.out.println ( "Query '" + q + "' retrieved " + res.total + " of " + res.totalFound + " matches in " + res.time + " sec." );
		System.out.println ( "Query stats:" );
		for ( int i=0; i<res.words.length; i++ )
		{
			SphinxWordInfo wordInfo = res.words[i];
			System.out.println ( "\t'" + wordInfo.word + "' found " + wordInfo.hits + " times in " + wordInfo.docs + " documents" );
		}

		System.out.println ( "\nMatches:" );
		for ( int i=0; i<res.matches.length; i++ )
		{
			SphinxMatch info = res.matches[i];
			System.out.print ( (i+1) + ". id=" + info.docId + ", weight=" + info.weight );

			if ( res.attrNames==null || res.attrTypes==null )
				continue;
//			String industry = getField("CI_Industry", info.docId+"");
//			System.out.print("CI_Industry:" + industry+"\t");
			for ( int a=0; a<res.attrNames.length; a++ )
			{
				System.out.print ( ", " + res.attrNames[a] + "=" );

				if ( ( res.attrTypes[a] & SphinxClient.SPH_ATTR_MULTI )!=0 )
				{
					System.out.print ( "(" );
					long[] attrM = (long[]) info.attrValues.get(a);
					if ( attrM!=null )
						for ( int j=0; j<attrM.length; j++ )
					{
						if ( j!=0 )
							System.out.print ( "," );
						System.out.print ( attrM[j] );
					}
					System.out.print ( ")" );

				} else
				{
					switch ( res.attrTypes[a] )
					{
						case SphinxClient.SPH_ATTR_INTEGER:
						case SphinxClient.SPH_ATTR_ORDINAL:
						case SphinxClient.SPH_ATTR_FLOAT:
						case SphinxClient.SPH_ATTR_BIGINT:
							/* longs or floats; print as is */
							System.out.print ( info.attrValues.get(a) );
							break;

						case SphinxClient.SPH_ATTR_TIMESTAMP:
							Long iStamp = (Long) info.attrValues.get(a);
							Date date = new Date ( iStamp.longValue()*1000 );
							System.out.print ( date.toString() );
							break;

						default:
							System.out.print ( "(unknown-attr-type=" + res.attrTypes[a] + ")" );
					}
				}
			}

			System.out.println();
		}
	}
	
}

/*
 * $Id: test.java 1316 2008-06-23 22:51:46Z shodan $
 */
