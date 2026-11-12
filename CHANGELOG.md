### LISTSHOP BFF

0.1.32 2026-11
Added
 * add checkedOffCount to ShoppingList, in the context of a list
 * add CheckOffItem use case

Changed
 * signature for BFFResult - for easier use in BFF and clients
 * Logout use case available offline, and returns TagTree
 * local list changed now saved only in session, not in db
 * updated github action files to use current java


0.1.24 - 0.1.27 2026-09
CICD tests

0.2.0 - 2026-09 
 * migrating existing use cases to server v2
 * new use case - SystemInitializeClient, GetCurrentList
 * ListManagement use cases - CreateList, GetList, UpdateList, DeleteList
 * Implemention for TagTree
