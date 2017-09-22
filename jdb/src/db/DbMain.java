package db;

import java.util.Collection;

import db.DbSelect.Change;
import db.DbSelect.Group;
import db.DbSelect.Where;
import db.DbTable.OpenStock;
import db.DbTable.Ponto;
import db.DbTable.Produto;

public class DbMain {

	public static void main(String[] args) {
		final DbTable<Ponto> pointTable = new DbTable<Ponto>();
		pointTable.insert(new Ponto(149, "REF"));
		pointTable.insert(new Ponto(150, "REF"));
		pointTable.insert(new Ponto(151, "REF"));
		final DbTable<Produto> productTable = new DbTable<Produto>();
		productTable.insert(new Produto("610", "Glp", 0.5));
		productTable.insert(new Produto("611", "Butano", 0.3));
		productTable.insert(new Produto("612", "Propano", 0.7));
		final DbTable<OpenStock> openStockTable = new DbTable<OpenStock>();
		openStockTable.insert(new OpenStock(20100601, 0, 149, "610", 90, 90));
		openStockTable.insert(new OpenStock(20100601, 0, 149, "611", 150, 120));
		openStockTable.insert(new OpenStock(20100601, 0, 149, "612", 150, 130));
		openStockTable.insert(new OpenStock(20100601, 0, 149, "613", 180, 180));
		openStockTable.insert(new OpenStock(20100601, 0, 150, "610", 100, 100));
		openStockTable.insert(new OpenStock(20100601, 0, 150, "611", 150, 120));
		openStockTable.insert(new OpenStock(20100601, 0, 150, "612", 150, 130));
		openStockTable.insert(new OpenStock(20100601, 0, 150, "613", 180, 180));
		openStockTable.insert(new OpenStock(20100601, 0, 152, "610", 100, 100));
		openStockTable.insert(new OpenStock(20100601, 0, 152, "611", 150, 120));
		openStockTable.insert(new OpenStock(20100601, 0, 152, "612", 150, 130));
		openStockTable.insert(new OpenStock(20100601, 0, 152, "613", 180, 180));
		openStockTable.insert(new OpenStock(20100602, 0, 149, "610", 100, 100));
		openStockTable.insert(new OpenStock(20100602, 0, 149, "611", 150, 120));
		openStockTable.insert(new OpenStock(20100602, 0, 149, "612", 150, 130));
		openStockTable.insert(new OpenStock(20100602, 0, 149, "613", 180, 180));
		openStockTable.insert(new OpenStock(20100602, 0, 150, "610", 100, 100));
		openStockTable.insert(new OpenStock(20100602, 0, 150, "611", 150, 120));
		openStockTable.insert(new OpenStock(20100602, 0, 150, "612", 150, 130));
		openStockTable.insert(new OpenStock(20100602, 0, 150, "613", 180, 180));
		openStockTable.insert(new OpenStock(20100602, 0, 152, "610", 100, 100));
		openStockTable.insert(new OpenStock(20100602, 0, 152, "611", 150, 120));
		openStockTable.insert(new OpenStock(20100602, 0, 152, "612", 150, 130));
		openStockTable.insert(new OpenStock(20100602, 0, 152, "613", 180, 180));
		DbSelect<OpenStock> resultTable = openStockTable
				.select(new OpenStock(20100601, 0, pointTable.first().id, productTable.first().id),
						new OpenStock(20100631, 0, pointTable.last().id, productTable.last().id)) //
				.where(new Where<OpenStock>() {
					@Override
					public boolean accept(OpenStock element) {
						if (element.mass == 0. && element.volume == 0.) {
							return false;
						}
						Produto produto = productTable.select(new Produto(element.productId)).first();
						if (produto == null) {
							return false;
						}
						Ponto point = pointTable.select(new Ponto(element.pointId)).first();
						if (point == null) {
							return false;
						}
						return true;
					}
				}) //
				.change(new Change<OpenStock>() {
					@Override
					public OpenStock change(OpenStock element) {
						return new OpenStock(element.day / 100, element.balanceId, element.pointId, element.productId, element.mass, element.volume);
					}
				}) //
				.group(new Group<OpenStock>() {
					@Override
					public OpenStock group(OpenStock key, Collection<OpenStock> group) {
						double mass = 0;
						double volume = 0;
						for (OpenStock item : group) {
							mass += item.mass;
							volume += item.volume;
						}
						return new OpenStock(key.day, key.balanceId, key.pointId, key.productId, mass, volume);
					}
				});
		for (OpenStock item : resultTable) {
			System.out.println(item);
		}
	}

}
