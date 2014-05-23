package org.apache.helix.controller.strategy.knapsack;

import java.util.ArrayList;

/**
 * Common implementation of a knapsack constraint satisfier<br/>
 * <br/>
 * Based on the C++ knapsack solver in Google's or-tools package.
 */
public abstract class AbstractKnapsackPropagator implements KnapsackPropagator {
  private ArrayList<KnapsackItem> _items;
  private long _currentProfit;
  private long _profitLowerBound;
  private long _profitUpperBound;
  private KnapsackState _state;

  /**
   * Initialize the propagator
   * @param state the current knapsack state
   */
  public AbstractKnapsackPropagator(final KnapsackState state) {
    _items = new ArrayList<KnapsackItem>();
    _currentProfit = 0L;
    _profitLowerBound = 0L;
    _profitUpperBound = Long.MAX_VALUE;
    _state = state;
  }

  @Override
  public void init(ArrayList<Long> profits, ArrayList<Long> weights) {
    final int numberOfItems = profits.size();
    _items.clear();
    for (int i = 0; i < numberOfItems; i++) {
      _items.add(new KnapsackItem(i, weights.get(i), profits.get(i)));
    }
    _currentProfit = 0;
    _profitLowerBound = Long.MIN_VALUE;
    _profitUpperBound = Long.MAX_VALUE;
    initPropagator();
  }

  @Override
  public boolean update(boolean revert, KnapsackAssignment assignment) {
    if (assignment.isIn) {
      if (revert) {
        _currentProfit -= _items.get(assignment.itemId).profit;
      } else {
        _currentProfit += _items.get(assignment.itemId).profit;
      }
    }
    return updatePropagator(revert, assignment);
  }

  @Override
  public long currentProfit() {
    return _currentProfit;
  }

  @Override
  public long profitLowerBound() {
    return _profitLowerBound;
  }

  @Override
  public long profitUpperBound() {
    return _profitUpperBound;
  }

  @Override
  public void copyCurrentStateToSolution(boolean hasOnePropagator, ArrayList<Boolean> solution) {
    if (solution == null) {
      throw new RuntimeException("solution cannot be null!");
    }
    for (KnapsackItem item : _items) {
      final int itemId = item.id;
      solution.set(itemId, _state.isBound(itemId) && _state.isIn(itemId));
    }
    if (hasOnePropagator) {
      copyCurrentStateToSolutionPropagator(solution);
    }
  }

  protected abstract void initPropagator();

  protected abstract boolean updatePropagator(boolean revert, final KnapsackAssignment assignment);

  protected abstract void copyCurrentStateToSolutionPropagator(ArrayList<Boolean> solution);

  protected KnapsackState state() {
    return _state;
  }

  protected ArrayList<KnapsackItem> items() {
    return _items;
  }

  protected void setProfitLowerBound(long profit) {
    _profitLowerBound = profit;
  }

  protected void setProfitUpperBound(long profit) {
    _profitUpperBound = profit;
  }
}
