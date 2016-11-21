/*
 * Copyright (c) 2014. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */
package com.higgses.griffin.database.sqlite;

import java.util.HashMap;
import java.util.Map.Entry;

import com.higgses.griffin.database.GinSqliteDB;
import com.higgses.griffin.database.table.ManyToOne;
import com.higgses.griffin.database.table.OneToMany;
import com.higgses.griffin.database.table.Property;
import com.higgses.griffin.database.table.TableInfo;

import android.database.Cursor;

public class CursorUtils
{

	public static <T> T getEntity(Cursor cursor, Class<T> clazz, GinSqliteDB db)
	{
		try
		{
			if (cursor != null)
			{
				TableInfo table = TableInfo.get(clazz);
				int columnCount = cursor.getColumnCount();
				if (columnCount > 0)
				{
					T entity = (T) clazz.newInstance();
					for (int i = 0; i < columnCount; i++)
					{

						String column = cursor.getColumnName(i);

						Property property = table.propertyMap.get(column);
						if (property != null)
						{
							property.setValue(entity, cursor.getString(i));
						}
						else
						{
							if (table.getId().getColumn().equals(column))
							{
								table.getId().setValue(entity, cursor.getString(i));
							}
						}

					}
					/**
					 * 处理OneToMany的lazyLoad形式
					 */
					for (OneToMany oneToManyProp : table.oneToManyMap.values())
					{
						if (oneToManyProp.getDataType() == OneToManyLazyLoader.class)
						{
							OneToManyLazyLoader oneToManyLazyLoader = new OneToManyLazyLoader(entity, clazz, oneToManyProp.getOneClass(), db);
							oneToManyProp.setValue(entity, oneToManyLazyLoader);
						}
					}

					/**
					 * 处理ManyToOne的lazyLoad形式
					 */
					for (ManyToOne manyToOneProp : table.manyToOneMap.values())
					{
						if (manyToOneProp.getDataType() == ManyToOneLazyLoader.class)
						{
							ManyToOneLazyLoader manyToOneLazyLoader = new ManyToOneLazyLoader(entity, clazz, manyToOneProp.getManyClass(), db);
							manyToOneLazyLoader.setFieldValue(cursor.getInt(cursor.getColumnIndex(manyToOneProp.getColumn())));
							manyToOneProp.setValue(entity, manyToOneLazyLoader);
						}
					}
					return entity;
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}

	public static DBModel getDbModel(Cursor cursor)
	{
		if (cursor != null && cursor.getColumnCount() > 0)
		{
			DBModel model = new DBModel();
			int columnCount = cursor.getColumnCount();
			for (int i = 0; i < columnCount; i++)
			{
				model.set(cursor.getColumnName(i), cursor.getString(i));
			}
			return model;
		}
		return null;
	}

	public static <T> T dbModel2Entity(DBModel dbModel, Class<?> clazz)
	{
		if (dbModel != null)
		{
			HashMap<String, Object> dataMap = dbModel.getDataMap();
			try
			{
				@SuppressWarnings("unchecked")
				T entity = (T) clazz.newInstance();
				for (Entry<String, Object> entry : dataMap.entrySet())
				{
					String column = entry.getKey();
					TableInfo table = TableInfo.get(clazz);
					Property property = table.propertyMap.get(column);
					if (property != null)
					{
						property.setValue(entity, entry.getValue() == null ? null : entry.getValue().toString());
					}
					else
					{
						if (table.getId().getColumn().equals(column))
						{
							table.getId().setValue(entity, entry.getValue() == null ? null : entry.getValue().toString());
						}
					}

				}
				return entity;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		return null;
	}

}
